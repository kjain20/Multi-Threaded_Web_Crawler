package edu.upenn.cis.ModelsTest;

public class ThreadPrints {
    public static void main(String[] args) {
        Thread thread1 = new Thread(){
            public void run()
            {
                for(int i=0;i<100;i++)
                {
                    System.out.println(i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        Thread thread2 = new Thread(){
            public void run()
            {
                for(int i=100;i>0;i--)
                {
                    System.out.println(i);
                    try {
                        Thread.sleep(105);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        thread1.start();
        thread2.start();
    }
}
