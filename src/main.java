public class main {
    public static void main(String[] args) {
        Thread mythread = new Thread(new Runnable() {
            @Override
            public void run() {
                new Webserver().startserver();
            }
        });
        mythread.start();
        System.out.println("thread started");

    }


}
