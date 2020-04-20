package earthview.ne.localserver.thread;

import earthview.ne.localserver.OfflineImageServer;


public class ImageServiceRunnable implements Runnable {
    private OfflineImageServer imageServer = null;

    @Override
    public void run() {
        if(null == imageServer) {
            imageServer = new OfflineImageServer();
        }
        try {
            imageServer.open();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        if(null != imageServer){
            imageServer.close();
            imageServer = null;
        }
    }
}
