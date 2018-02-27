package utilclass;

import java.util.BitSet;

/**
 * @Author: spider_hgyi
 * @Date: Created in 下午6:38 18-2-5.
 * @Modified By:
 * @Description: 布隆过滤器
 */
public class BloomFilter {
    // 设置布隆过滤器的容量为2的25次方，也就是此布隆过滤器大概最少可以处理百万级别的数据
    private static final int DEFAULT_SIZE = 2 << 24;
    // 产生随机数的种子，可产生6个不同的随机数产生器
    private static final int[] seeds = new int[]{7, 11, 13, 31, 37, 61};
    // Java中的按位存储的思想，其算法的具体实现（布隆过滤器）
    private static BitSet bits = new BitSet(DEFAULT_SIZE);

    // 得到此 value 所产生的六个信息指纹
    public int[] getFingerprint(String value) {
        int result = 0;
        int[] fingerprints = new int[6];
        for (int i = 0; i < seeds.length; i++) {
            for (int j = 0; j < value.length(); j++) {
                result = seeds[i] * result + value.charAt(j);
            }

            result = (DEFAULT_SIZE - 1) & result;
            fingerprints[i] = result;
        }

        return fingerprints;
    }

    // 判断url是否已经存在于布隆过滤器中
    public boolean isExist(int[] fingerprints) {
        boolean ret = true;

        for (int fingerprint : fingerprints) {
            // 只有六个标志位都为true，才能判断这个url<!可能!>在这个集合中（此处存在误判）
            ret = ret && bits.get(fingerprint);
        }

        return ret;
    }

    // 将url存储进布隆过滤器中
    public void saveFingerprints(int[] fingerprints) {
        for (int fingerprint : fingerprints) {
            bits.set(fingerprint);
        }
    }
}