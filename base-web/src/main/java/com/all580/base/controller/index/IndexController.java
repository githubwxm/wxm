package com.all580.base.controller.index;

import com.aliyun.mns.model.SubscriptionMeta;
import com.all580.base.manager.MnsEventCache;
import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.ep.api.service.EpService;
import com.all580.notice.api.service.SmsService;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.OrderService;
import com.all580.payment.api.service.EpPaymentConfService;
import com.all580.payment.api.service.ThirdPayService;
import com.all580.product.api.service.ProductRPCService;
import com.all580.report.api.ReportConstant;
import com.all580.report.api.service.QueryOrderService;
import com.all580.report.api.service.ReportExportTaskService;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.io.cache.redis.RedisUtils;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.lang.exception.ApiException;
import javax.lang.exception.ParamsMapValidationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

@Controller
@Slf4j
public class IndexController extends BaseController {
	@Autowired
	private SmsService smsService;

	@Autowired
	private MnsEventCache mnsEventCache;
	@Autowired
	private ThirdPayService thirdPayService;
	@Autowired
	private EpService epService;
	@Autowired
	private EpPaymentConfService epPaymentConfService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private VoucherRPCService voucherRPCService;
	@Autowired
	private ProductRPCService productRPCService;
	@Autowired
	private QueryOrderService queryOrderService;
	@Autowired
	private CoreEpAccessService coreEpAccessService;
	@Autowired
	private TopicPushManager topicPushManager;
	@Autowired
	private RedisUtils redisUtils;
	@Value("${mns.notify.top}")
	private String topicName;

	@Autowired
	private ReportExportTaskService reportExportTaskService;

	@RequestMapping(value = "api/homepage/order/statistics", method = RequestMethod.GET)
	@ResponseBody
	public Result indexData(Integer ep_id){
		Result result = new Result(true);
		Map map = JsonUtils.json2Map( redisUtils.get(ReportConstant.INDEX_DATA+ep_id));
		Date now = new  Date();
		String nowNum = redisUtils.get( String.format("%s%d:%s", OrderConstant.INDEX_DATA_CHANGE, ep_id,
				DateFormatUtils.DATE_FORMAT.format(now)));
		String toNum = redisUtils.get(String.format("%s%d:%s", OrderConstant.INDEX_DATA_CHANGE, ep_id,
				DateFormatUtils.DATE_FORMAT.format(DateUtils.addDays(now,1))));
		 if(nowNum!=null || toNum!=null){
		 	if(map==null){
		 		map = new HashMap();
		 		List list = new ArrayList();
				List<String> typeList = Lists.newArrayList("5101","5102","5103");
				List<String> days = new ArrayList<>();
				Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
				for(int i=7;i>0;i--){
					ca.setTime(new Date()); //设置时间为当前时间
					ca.add(Calendar.DATE, -i); //年份减1
					Date lastMonth = ca.getTime(); //结果
					String start=  DateFormatUtils.converToStringDate(lastMonth);
					days.add(start);
				}
				for(String type:typeList){
					Map m = new HashMap();
					m.put("ticket_type",type);
					List<Map> initList =new ArrayList<>();
					for(String day:days){
						initList.add(getDayMap(day));
					}
					Map data_info = new HashMap();
					data_info.put("report_infos",initList);
					m.put("data_info",data_info);
					list.add(m);

				}
				map.put("list",list);
			}
			 redisUtils.set(ReportConstant.INDEX_DATA+ep_id,map);
		 	if(map.get("list")!=null){
				List<Map<String,Object>> list =(List<Map<String,Object>>) map.get("list");
				for(Map m:list){
					Integer type = CommonUtil.objectParseInteger(m.get("ticket_type")) ;
					if(type.intValue()- ReportConstant.ProductType.SCENERY==0){
						Map data_info =(Map) m.get("data_info");
						Map today_infos = new HashMap();
						today_infos.put("nums",nowNum==null?0:nowNum);
						Map tomorrow_infos = new HashMap();
						tomorrow_infos.put("nums",toNum==null?0:toNum);
						data_info.put("today_infos",today_infos);
						data_info.put("tomorrow_infos",tomorrow_infos);
					}
				}
			}
		 }
        result.put(map);
		return result;
	}
	private Map getDayMap(String date){
		Map dayMap = new HashMap();
		dayMap.put(date,date);
		dayMap.put("nums",0);
		dayMap.put("money",0);
		return  dayMap;
	}
	@RequestMapping(value = "report/export/task/down", method = RequestMethod.GET)
	public String downFile(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int id){
		// return "redirect:http://www.baidu.com";
		Result  result= reportExportTaskService.downFile(id);
		if(result.isSuccess()){
			String url =(String) result.get();
			//return  new ModelAndView(new RedirectView(url));
			return "redirect:"+url;
		}else{
			reportExportTaskService.updateStart(id);
			return "";
		}
	}
	@RequestMapping(value = "sms/set", method = RequestMethod.GET)
	@ResponseBody
	public Result setSms(HttpServletRequest request, HttpServletResponse response, @RequestParam boolean is){
		Result result=smsService.setIsSend(is);
		result.put(is);
		return result;
	}

	@RequestMapping(value = "events")
	@ResponseBody
	public Result listEvents() {
		Result<Map> result = new Result<>(true);
		result.put(mnsEventCache.getCacheEventsMap().asMap());
		return result;
	}

	@RequestMapping(value = "api/signature/wx", method = RequestMethod.POST)
	@ResponseBody
	public Result signatureForWx(@RequestBody Map params) {
		Result<Map> result = new Result<>(true);
        Integer coreEpId = CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID));
        String url = CommonUtil.objectParseString(params.get("url"));
        if (StringUtils.isEmpty(url)) {
        	throw new ParamsMapValidationException("url 不能为空");
		}
		url = url.replaceAll("#(.*)", "");
        Result<Map> tokenResult = thirdPayService.getWxAccessToken(coreEpId);
        if (!tokenResult.isSuccess()) {
            throw new ApiException("获取微信token失败");
        }
        Map map = tokenResult.get();
        String nonceStr = DigestUtils.md5Hex(RandomStringUtils.randomAlphanumeric(10));
        long timestamp = System.currentTimeMillis() / 1000;
        String signature = DigestUtils.sha1Hex(String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s", map.get("ticket"), nonceStr, timestamp, url));
        result.put("app_id", map.get("appId"));
        result.put("timestamp", timestamp);
        result.put("noncestr", nonceStr);
        result.put("signature", signature);
		return result;
	}

	@RequestMapping(value = "api/openid/wx", method = RequestMethod.POST)
	@ResponseBody
	public Result openidForWx(@RequestBody Map params) {
        Integer coreEpId = CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID));
		Object code = params.get("code");
		if (code == null) {
			throw new ParamsMapValidationException("code 不能为空");
		}
		Result<Map> tokenResult = thirdPayService.getWxOpenid(coreEpId, code.toString());
        if (!tokenResult.isSuccess()) {
            throw new ApiException("获取微信openid失败");
        }
		return tokenResult;
	}

	@RequestMapping(value = "api/sync/subscribe", method = RequestMethod.POST)
	@ResponseBody
	public Result subscribeSync(@RequestBody Map params) {
		validateSubscribe(params);
		boolean test = BooleanUtils.toBoolean(params.get("test").toString());
		String accessId = params.get("access_id").toString();
		Map<String, Object> map = new HashMap<>();
		map.put("access_id", accessId);
		Result<Map<String, Object>> result = coreEpAccessService.selectAccess(map);
		if (!result.isSuccess())
			return result;
		String accessKey = result.get().get("access_key").toString();
		String url = params.get("url").toString();
		if (url.endsWith("/")) {
			url = url + "service/mns/client/home/index";
		} else {
			url = url + "/service/mns/client/home/index";
		}
		topicPushManager.subscribeSync(topicName, params.get("name").toString(), url, accessKey,
				test ? SubscriptionMeta.NotifyStrategy.BACKOFF_RETRY : SubscriptionMeta.NotifyStrategy.EXPONENTIAL_DECAY_RETRY);
		return new Result(true);
	}

	@RequestMapping(value = "heartbeat", method = RequestMethod.POST)
	@ResponseBody
	public Result heartbeat(HttpServletResponse response) {
		Result result = new Result(true);
		Map<String, Long> map = new HashMap<>();
		map.put("ep", heartbeat(epService, response));
		map.put("payment", heartbeat(epPaymentConfService, response));
		map.put("voucher", heartbeat(voucherRPCService, response));
		map.put("product", heartbeat(productRPCService, response));
		map.put("order", heartbeat(orderService, response));
		map.put("report", heartbeat(queryOrderService, response));
		result.put(map);
		return result;
	}

	private long heartbeat(Object service, HttpServletResponse response) {
		long start = System.currentTimeMillis();
		try {
			Method method = null;
			for (Class<?> aClass : service.getClass().getInterfaces()) {
				if (aClass.getName().startsWith("com.all580.")) {
					try {
						method = aClass.getMethod("heartbeat");
					} catch (NoSuchMethodException ignored) {}
				}
			}
			if (method == null) {throw new NoSuchMethodException();}
			method.invoke(service);
		} catch (Exception e) {
			log.warn("heartbeat error", e);
			response.setStatus(504);
			return -1;
		}
		return System.currentTimeMillis() - start;
	}

	private void validateSubscribe(Map params) {
		Map<String[], ValidRule[]> rules = new HashMap<>();
		rules.put(new String[]{
				"name",
				"url",
				"access_id",
				"test"
		}, new ValidRule[]{new ValidRule.NotNull()});

		rules.put(new String[]{
				"test"
		}, new ValidRule[]{new ValidRule.Boolean()});

		rules.put(new String[]{
				"url"
		}, new ValidRule[]{new ValidRule.Pattern("[a-zA-z]+://[^\\s]*")});

		ParamsMapValidate.validate(params, rules);
	}
}
