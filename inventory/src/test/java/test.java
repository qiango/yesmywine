//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
///**
// * Created by SJQ on 2017/1/17.
// *
// * @Description:
// */
//public class test {
//    public int max(int[] data, ExecutorService service) throws InterruptedException, ExecutionException {
//        if (data.length == 1) {
//            return data[0];
//        } else if (data.length == 0) {
//            throw new IllegalArgumentException();
//        }
//        //将任务分解为两部分
//        FindMaxTask task1 = new FindMaxTask(data, 0, data.length / 2);
//        FindMaxTask task2 = new FindMaxTask(data, data.length / 2, data.length);
//        //创建2个线程
//        Future<Integer> f1 = service.submit(task1);
//        Future<Integer> f2 = service.submit(task2);
//        service.shutdown();
//        return Math.max(f1.get(), f2.get());
//    }
//
//    public static void main(String[] args) {
//        ExecutorService service = Executors.newFixedThreadPool(2);
//        try {
//            test m = new test();
//            int[] numArr = {345, 213, 45, 675, 127, 478, 456};
//            System.out.println(m.max(numArr, service));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            service.shutdown();
//        }
//    }
//}
