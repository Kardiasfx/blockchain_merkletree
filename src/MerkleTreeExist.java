import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

//梅克尔树格式(sorted),图2
//           1
//     3           2
//  7     6     5    4
//15 14 13 12 11 10 9 8


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Input an integer n to generate 2^n numbers:");
        int n = sc.nextInt();
        System.out.println("Input an integer seed to control the generation of the numbers:");
        int seed = sc.nextInt();
        // 生成 2^n 个数据，数组长度为 2^(n+1)-1
        String[] merkleTree = getMerkleTree(n, seed);
        System.out.println("Input a number recorded in the tree to show its location and verification:");
        verifyExist(merkleTree, sc.nextInt());

        sc.close();
    }

    public static Integer[] genIntegers(int n, int seed) {
        Random random = new Random(seed);
        List<Integer> integers = new ArrayList<>((int) Math.pow(2, n)); // 2的n次方
        for (int i = 0; i < (int) Math.pow(2, n); i++) {
            int randomInt = random.nextInt();
            System.out.println("random" + i + ":" + randomInt);
            integers.add(randomInt);
        }
        return integers.toArray(new Integer[0]);
    }

    public static String getHash(String str) throws NoSuchAlgorithmException {
        // 创建MD5消息摘要对象
        MessageDigest md = MessageDigest.getInstance("MD5");

        // 计算数据的MD5哈希值
        byte[] md5Bytes = md.digest(str.getBytes());

        // 将字节数组转换为十六进制字符串表示
        StringBuilder hexString = new StringBuilder();
        for (byte b : md5Bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static String[] getMerkleTree(int n, int seed) {
        int dataLength = (int) Math.pow(2, n+1);
        String[] merkleArray = new String[dataLength];
        Integer[] arrayNumbers = genIntegers(n, seed);
        for (int i = dataLength - 1, j = 0; i >= (int) Math.pow(2, n); i--, j++) {
            try {
                merkleArray[i] = getHash(arrayNumbers[j].toString());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        for (int i = dataLength - 1; merkleArray[1] == null; i-=2) {
            try {
                merkleArray[i/2] = getHash(merkleArray[i] + merkleArray[i-1]);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
//        merkle树数组输出
        for (int i = 0; i < merkleArray.length; i++) {
            System.out.println(i + ":" + merkleArray[i]);
        }
        return merkleArray;
    }

    public static void verifyExist(String[] merkleTree, int numberExist) {
        String rootHash = merkleTree[1];
//        String[] path = new String[(int) Math.log(merkleTree.length)];
        List<String> path = new ArrayList<>();
        StringBuilder position = new StringBuilder();
        try {
            String hashExist = getHash(Integer.toString(numberExist));
            for (int i = merkleTree.length - 1; i >= merkleTree.length / 2; i--) {
                int num = i;
                while (hashExist.equals(merkleTree[i])) {
                    if (num == 1) {
                        System.out.println("evidence, known roothash: " + rootHash);
                        break;
                    }
                    else if (num % 2 == 1) {
                        System.out.println("evidence, node" + (num - 1) + ": " + merkleTree[num - 1]);
                        position.insert(0, "0");
                        path.add(0, merkleTree[num - 1]);
                    }
                    else {
                        System.out.println("evidence, node" + (num + 1) + ": " + merkleTree[num + 1]);
                        position.insert(0, "1");
                        path.add(0, merkleTree[num + 1]);
                    }
                    num /= 2;
                }
            }
            System.out.println("Number " + numberExist + " was found! Position(binary): " + position);
            System.out.println("Things you will need to find the evidence: ");
            for (String str : path) System.out.println(str);
            System.out.println(verify(hashExist, position.toString(), path.toArray(new String[0]), rootHash));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String hashExist, String path, String[] givenData, String rootHash) {
        for (int i = givenData.length - 1; i >= 0; i--) {
            try {
                if (path.endsWith("0")) {
                    hashExist = getHash(hashExist + givenData[i]);
                }
                else {
                    hashExist = getHash(givenData[i] + hashExist);
                }
                path = path.substring(0, path.length() - 1);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return hashExist.equals(rootHash);
    }

}