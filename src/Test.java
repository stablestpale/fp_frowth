/**
 * @author zzy
 * @description
 * @date 2022/4/14 21:48
 */


public class Test {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Integer minAppear = 2000;
        // txtTest txtOnlineRetail txtChainStoreFIM
        String fileName = "src/data/txtOnlineRetail.txt";
        FPGrowth fpGrowth = new FPGrowth(minAppear, fileName);
        fpGrowth.print();
        System.out.println("\n运行时间: " + (System.currentTimeMillis() - startTime) / 1000 + "s");
    }

}
