package cn.ocoop.shiro.filter.authc;

import cn.ocoop.shiro.session.mgt.ShiroSessionDaoRedisAdapter;
import cn.ocoop.shiro.utils.RequestUtil;
import cn.ocoop.spring.App;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by liolay on 16-6-20.
 */
public class RateLimitWithCaptchaFilter extends AccessControlFilter {
    public static final String SUBMIT_CAPTCHA = App.getBean(Environment.class).getProperty("shiro.captcha.header", "Submit-Captcha");
    private static final DateTimeFormatter SEND_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String FREQUENCY_KEY = "send_captcha_times";
    private Long rateLifeTime = 3600000l;//一小时
    private Long maxRate = 10l;

    public RateLimitWithCaptchaFilter() {
    }

    public RateLimitWithCaptchaFilter(Long maxRate, Long rateLifeTime) {
        this.rateLifeTime = rateLifeTime;
        this.maxRate = maxRate;
    }

    private static Session getSession() {
        return SecurityUtils.getSubject().getSession(false);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        if (getSession() == null) throw new InvalidRateParamException("未发现有效的会话,该请求不允许访问");
        return rateUnderflow() || correctCaptcha(request);
    }

    private boolean correctCaptcha(ServletRequest request) {
        String receivedCaptcha = getReceivedCaptcha(request);
        String sessionCaptcha = getAndClearSessionCaptcha();
        if (StringUtils.isAnyBlank(sessionCaptcha, receivedCaptcha) || !sessionCaptcha.equalsIgnoreCase(receivedCaptcha))
            return false;
        return true;
    }

    private boolean rateUnderflow() {
        RedisTemplate redisTemplate = App.getBean(RedisTemplate.class);
        String rateKey = getRatePrefix() + ":" + Instant.now().getEpochSecond();
        long reachTimes = getReachTimes(redisTemplate);
        if (reachTimes > maxRate) return false;

        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.multi();
                K key = (K) (rateKey);
                operations.opsForValue().set(key, (V) LocalDateTime.now().format(SEND_TIME_FORMAT));
                operations.expire(key, rateLifeTime, TimeUnit.MILLISECONDS);
                return operations.exec();
            }
        });
        return true;
    }

    private long getReachTimes(RedisTemplate redisTemplate) {
        Set keys = redisTemplate.keys(getRatePrefix() + "*");
        if (CollectionUtils.isEmpty(keys)) return 1;
        return keys.size() + 1;
    }

    private String getRatePrefix() {
        return App.getBean(ShiroSessionDaoRedisAdapter.class).getRedisSessionKey(getSession().getId()) + ":" + FREQUENCY_KEY;
    }

    private String getReceivedCaptcha(ServletRequest request) {
        return WebUtils.toHttp(request).getHeader(SUBMIT_CAPTCHA);
    }

    private String getAndClearSessionCaptcha() {
        return (String) getSession().removeAttribute(SUBMIT_CAPTCHA);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        RequestUtil.response(response, RequestUtil.SC_INVALID_CAPTCHA, "请求太频繁");
        return false;
    }

}
