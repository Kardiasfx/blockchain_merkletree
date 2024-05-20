import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UniqueIntegers {

    public static List<Integer> generateDistinctRandomIntegers(int n) {
        Random random = new Random();
        List<Integer> integers = new ArrayList<>(2 << n); // 2的n次方
        for (int i = 0; i < 2 << n; i++) {
            int randomInt;
            do {
                randomInt = random.nextInt(); // 生成一个随机整数
            } while (integers.contains(randomInt)); // 确保不会有重复
            integers.add(randomInt);
        }
        return integers;
    }

    public static void main(String[] args) {
        int n = 5; // 举个例子，2的8次方等于256
        List<Integer> integers = generateDistinctRandomIntegers(n);
        // 输出结果
        for (Integer integer : integers) {
            System.out.println(integer);
        }
    }
}