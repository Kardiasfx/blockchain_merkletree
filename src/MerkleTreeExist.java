import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

//梅克尔树格式(sorted),图2
//           1
//     3           2
//  7     6     5    4
//15 14 13 12 11 10 9 8


public class MerkleTreeExist {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Input an integer n to generate 2^n numbers:");
        int n = sc.nextInt();
        System.out.println("Input an integer seed to control the generation of the numbers:");
        int seed = sc.nextInt();
        // 生成 2^n 个数据，数组长度为 2^(n+1)-1
        System.out.println("Do you need to sort the numbers? If yes, input 1, else input 0(If you want to find a number not" +
                "existing in the tree, input 1):");
        int flag = sc.nextInt();
        Integer[] integers = genIntegers(n, seed, flag);
        String[] merkleTree = getMerkleTree(integers);
        System.out.println("Input a number recorded in the tree to show its location and verification:");
        int numberExist = sc.nextInt();
        if (!verifyExist(merkleTree, numberExist)) {
            Integer[] notExistEvidence = search(integers, numberExist);
            verifyExist(merkleTree, notExistEvidence[0]);
            verifyExist(merkleTree, notExistEvidence[1]);
            if (numberExist < notExistEvidence[0]) System.out.println("Number " + numberExist + " was not found! " +
                    "Because the number " + notExistEvidence[0] + " is the smallest number in the tree.");
            else if (numberExist > notExistEvidence[1]) System.out.println("Number " + numberExist + " was not found! " +
                    "Because the number " + notExistEvidence[1] + " is the biggest number in the tree.");
            else System.out.println("Number " + numberExist + " was not found! Because the two numbers " + notExistEvidence[0]
                        + " and " + notExistEvidence[1] + " are in the tree and they are next to each other.");
        }
        sc.close();
    }

    public static Integer[] genIntegers(int n, int seed, int flag) {
        Random random = new Random(seed);
        List<Integer> integers = new ArrayList<>((int) Math.pow(2, n)); // 2的n次方
        for (int i = 0; i < (int) Math.pow(2, n); i++) {
            int randomInt = random.nextInt();
            integers.add(randomInt);
        }
        if (flag == 1) Collections.sort(integers);
        for (int i = 0; i < (int) Math.pow(2, n); i++) System.out.println("random number " + i + ": " + integers.get(i));
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

    public static String[] getMerkleTree(Integer[] integers) {
        int dataLength = 2 * integers.length;
        String[] merkleArray = new String[dataLength];
        for (int i = dataLength - 1, j = 0; i >= integers.length; i--, j++) {
            try {
                merkleArray[i] = getHash(integers[j].toString());
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

    public static boolean verifyExist(String[] merkleTree, int numberExist) {
        String rootHash = merkleTree[1];
//        String[] path = new String[(int) Math.log(merkleTree.length)];
        List<String> path = new ArrayList<>();
        StringBuilder position = new StringBuilder();
        int flag = 0;
        try {
            String hashExist = getHash(Integer.toString(numberExist));
            for (int i = merkleTree.length - 1; i >= merkleTree.length / 2; i--) {
                int num = i;
                while (hashExist.equals(merkleTree[i])) {
                    flag = 1;
                    if (num == 1) {
                        System.out.println("evidence, known root hash: " + rootHash);
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
            if (flag == 1) {
                System.out.println("Number " + numberExist + " was found! Position(binary): " + position);
                System.out.println("Things you will need to find the evidence: ");
                for (String str : path) System.out.println(str);
                System.out.println("Existence of number " + numberExist + " :" +
                        verify(hashExist, position.toString(), path.toArray(new String[0]), rootHash));
                return true;
            }
            else {
                System.out.println("Number " + numberExist + " was not found!");
                return false;
            }

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String hashExist, String path, String[] givenData, String rootHash) {
        for (int i = givenData.length - 1; i >= 0; i--) {
            try {
                if (path.endsWith("0")) { // evidence on the right
                    hashExist = getHash(hashExist + givenData[i]);
                }
                else { // evidence on the left
                    hashExist = getHash(givenData[i] + hashExist);
                }
                path = path.substring(0, path.length() - 1);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return hashExist.equals(rootHash);
    }

    public static Integer[] search(Integer[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (nums[mid] > target) right = mid; else left = mid;
            if (left == right - 1) return new Integer[] {nums[left], nums[right]};
        }
        return new Integer[] {left, right};
    }

}