package com.all580.voucherplatform.utils.voucher;

import com.framework.common.io.cache.redis.RedisUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Linv2 on 2017-05-26.
 */
@Service
public class VoucherGenerate {
    private DecimalFormat df;
    private SimpleDateFormat sdf;
    private String script;
    private String voucherSeqKey = "TEST:UUID:A";
    @Autowired
    private RedisUtils redisUtils;

    public VoucherGenerate() {

        df = new DecimalFormat("000");
        sdf = new SimpleDateFormat("yyMMdd");
        script = "local seq = redis.call('incr',KEYS[1])\n" +
                "if seq > 999 then\n" +
                "    redis.call('set',KEYS[1], 1)\n" +
                "    return 1\n" +
                "end\n" +
                "return seq";
    }

    /**
     * 生成二维码
     *
     * @param size    长度
     * @param prefix  字符前缀
     * @param postfix 字符后缀
     * @return
     */
    public String getVoucher(Integer size, String prefix, String postfix) {
        size = 16;
        Integer prefixSize = StringUtils.isEmpty(prefix) ? 0 : prefix.length();
        Integer postfixSize = StringUtils.isEmpty(postfix) ? 0 : postfix.length();
        Integer randomSize = size - prefixSize - postfixSize - 3 - 6;
        long seq = (long) redisUtils.eval(script, voucherSeqKey, Collections.singletonList(voucherSeqKey), null);
        char[] id = df.format(seq).toCharArray();//3位序号符
        char[] date = sdf.format(new Date()).toCharArray();//6位日期
        char[] random = RandomStringUtils.randomNumeric(randomSize).toCharArray();
        int randIdx = 0;
        char[] tmp = new char[]{
                date[0],
                date[2],
                random[randIdx++],
                date[1],
                id[2],
                date[3],
                random[randIdx++],
                date[5],
                id[0],
                date[4],
                random[randIdx++],
                id[1]
        };

        StringBuilder builder = new StringBuilder();
        if (!StringUtils.isEmpty(prefix))
            builder.append(prefix);
        prefixSize = 2 - prefixSize;
        while (prefixSize > 0) {
            builder.append(random[randIdx++]);
            prefixSize--;
        }
        builder.append(tmp);

        postfixSize = 2 - postfixSize;
        while (postfixSize > 0) {
            builder.append(random[randIdx++]);
            postfixSize--;
        }
        if (!StringUtils.isEmpty(postfix))
            builder.append(postfix);
        return builder.toString();
    }


}
