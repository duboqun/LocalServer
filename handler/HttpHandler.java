package earthview.ne.localserver.handler;

import android.os.Environment;

import earthview.ne.dbhelper.DBHelper;
import earthview.ne.localserver.utils.PropertiesUtil;

import java.io.File;
import java.io.FileInputStream;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpHandler extends SimpleChannelInboundHandler<Object> {

    private int x, y, z;
    private int tuzu,bianhao;
    private boolean keepAlive;

    public HttpHandler() {
        super();
        System.out.printf("控制器 %s 被创建.\n", this.toString());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        System.out.printf("控制器 %s 销毁.\n", this.toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        System.out.printf("控制器 %s 读取一个包.\n", this.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.printf("控制器 %s 出现异常.\n", this.toString());
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {

            HttpRequest request = (HttpRequest) msg;
            String uri = request.getUri();
            //解析请求url
            /* "/1/1/2/1/2.png"*/
            if(null != uri && uri.endsWith(PropertiesUtil.getProperty("tile_format"))) {
                String path = uri.split("\\.")[0];
                String[] levelArr = path.split(PropertiesUtil.getProperty("slash"));
                tuzu=Integer.parseInt(levelArr[1]);
                bianhao=Integer.parseInt(levelArr[2]);
                z = Integer.parseInt(levelArr[3]);
                x = Integer.parseInt(levelArr[4]);
                y = Integer.parseInt(levelArr[5]);
            }

            if (request.getMethod() != HttpMethod.GET) {
                throw new IllegalStateException("请求不是GET请求.");
            }

            if (HttpHeaders.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }

            keepAlive = HttpHeaders.isKeepAlive(request);
        }
//实例是从本地获取文件，应改为从数据获取文件

        if(msg instanceof LastHttpContent){

            StringBuilder sb = new StringBuilder();
//            String localStorage = Environment.getDataDirectory().getPath();
//            sb.append(localStorage);
//            sb.append(PropertiesUtil.getProperty("slash"));
            sb.append(PropertiesUtil.getProperty("offline_tile_path"));
            sb.append(tuzu);
            sb.append(PropertiesUtil.getProperty("slash"));
            sb.append(bianhao);
            //sb.append(PropertiesUtil.getProperty("slash"));
            //sb.append("省会城区图");
            sb.append(PropertiesUtil.getProperty("offline_tile_format"));
            File f = new File(sb.toString());   //sdcard/android/anhui/1/1/省会城区图.mbtiles
            if(f.exists()){
//                int len = (int)f.length();
//                byte[] fileContent = new byte[len];
//                FileInputStream fis = new FileInputStream(f);
//                fis.read(fileContent);
//                fis.close();
                DBHelper dbHelper=new  DBHelper();
                byte[] fileContent=  dbHelper.getTileImage(sb.toString(),x,y,z);
//                dbHelper.connect(sb.toString());
//                byte[] fileContent=dbHelper.selectquery(x,y,z);
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(fileContent));
                response.headers().set(CONTENT_TYPE, PropertiesUtil.getProperty("offline_tile_response_content_type"));
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

                if (!keepAlive) {
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                    ctx.writeAndFlush(response);
                }
            }
        }
    }
}
