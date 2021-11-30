 package atento.ripley;
 
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import org.apache.commons.daemon.Daemon;
 import org.apache.commons.daemon.DaemonContext;
 import org.apache.commons.daemon.DaemonInitException;
 
 
 
 public class app
   implements Daemon
 {
   private ExecutorService executorService = Executors.newSingleThreadExecutor();
 
 
   
   public void init(DaemonContext context) throws DaemonInitException, Exception {}
 
   
   public void start() throws Exception {
     this.executorService.execute(new Runnable()
         {
           CountDownLatch latch = new CountDownLatch(1);
           
           public void run() {
             try {
               NetClientPostGet.main(null);
             } catch (Exception exception) {}
           }
         });
   }
 
 
   
   public void stop() throws Exception {
     this.executorService.shutdown();
   }
   
   public void destroy() {}
 }


