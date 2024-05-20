import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Example {
    public static void main(String[] args) {
        String data = "f56e335aac1bcdc3b1b2ccfd8ba0ae33b4ae99b7be7cf03e4cb34fd8764176ff";

        try {
            // 创建MD5消息摘要对象
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 计算数据的MD5哈希值
            byte[] md5Bytes = md.digest(data.getBytes());

            // 将字节数组转换为十六进制字符串表示
            StringBuilder hexString = new StringBuilder();
            for (byte b : md5Bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            String md5Hash = hexString.toString();
            System.out.println("MD5 哈希值: " + md5Hash);

        } catch (NoSuchAlgorithmException e) {
            System.err.println("MD5 算法不可用: " + e.getMessage());
        }
    }
}
