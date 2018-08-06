package ru.mera.count.controller;

import org.junit.Test;
import org.springframework.util.Assert;
import ru.mera.count.model.CountWrapper;
import ru.mera.count.model.WhoModel;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

public class CountControllerTest extends ControllerTestConstant{

	@Test
	public void test_getCount(){

		CountWrapper result =
			given().
			when().
				get(countUrl + "/count").as(CountWrapper.class);

		Assert.notNull(result, "result is null");
		Assert.notNull(result.getTimestamp(), "timeStamp is empty");
	}

	@Test
	public void test_increaseCount(){
		int delta = 19;

		CountWrapper before =
			given().
			when().
				get(countUrl + "/count").as(CountWrapper.class);

		CountWrapper result =
			given().
				pathParams("delta", delta).
			when().
				put(countUrl + "/count/plus/{delta}").as(CountWrapper.class);

		Assert.notNull(result, "result is null");
		Assert.notNull(result.getTimestamp(), "timeStamp is empty");
		Assert.isTrue(result.getCount() - before.getCount() == delta, "wrong result");
	}

	@Test
	public void test_decreaseCount(){
		int delta = 25;

		CountWrapper before =
				given().
						when().
						get(countUrl + "/count").as(CountWrapper.class);

		CountWrapper result =
				given().
						pathParams("delta", delta).
						when().
						put(countUrl + "/count/minus/{delta}").as(CountWrapper.class);

		Assert.notNull(result, "result is null");
		Assert.notNull(result.getTimestamp(), "timeStamp is empty");
		Assert.isTrue(before.getCount() - result.getCount() == delta, "wrong result");
	}

	@Test
	public void test_setCount(){
		WhoModel model = new WhoModel();
		model.setWho("Bobby");
		model.setCount(256);

		CountWrapper before =
			given().
			when().
				get(countUrl + "/count").as(CountWrapper.class);
		Assert.notNull(before, "result is null");
		Assert.notNull(before.getTimestamp(), "timeStamp is empty");

		CountWrapper result =
			given().
				contentType("application/json").
				body(model).
			when().
				post(countUrl + "/count").as(CountWrapper.class);

		Assert.notNull(result, "result is null");
		Assert.notNull(result.getTimestamp(), "timeStamp is empty");
		Assert.isTrue(result.getCount() == model.getCount(), "wrong result");

		CountWrapper testResult =
			given().
			when().
				get(countUrl + "/count").as(CountWrapper.class);

		Assert.notNull(testResult, "result is null");
		Assert.notNull(testResult.getTimestamp(), "timeStamp is empty");
		Assert.isTrue(result.getCount() == testResult.getCount(), "wrong result");
	}

	@Test
	public void test_dropCount() {
		CountWrapper result =
			given().
			when().
				delete(countUrl + "/count").as(CountWrapper.class);

		Assert.notNull(result, "result is null");
		Assert.notNull(result.getTimestamp(), "timeStamp is empty");
		Assert.isTrue(result.getCount() == 0, "wrong result");
	}

	@Test
	public void test_multiThreading(){
		ExecutorService executorService = Executors.newFixedThreadPool(300);

		for (int i = 0; i < 110; i++) {
			executorService.submit(new PutRequest());
		}

		sleep(1000);

		for (int i = 0; i < 110; i++){
			executorService.submit(new GetRequest());
		}

		sleep(1000);

		for (int i = 0; i < 110; i++){
			executorService.submit(new PostRequest());
		}

		executorService.shutdown();
		try {
			executorService.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

//		try {
//			if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
//				executorService.shutdownNow(); // Cancel currently executing tasks
//				if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
//					System.err.println("Pool did not terminate");
//			}
//		} catch (InterruptedException ie) {
//			executorService.shutdownNow();
//			Thread.currentThread().interrupt();
//		}
	}

	private void sleep(int ms){
		try {
			Thread.sleep(ms);
			System.out.println("Sleep  " + ms + "  milliseconds");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static class PutRequest implements Callable<CountWrapper>{
		@Override
		public CountWrapper call() throws Exception {
			int delta = 19;

			CountWrapper before =
					given().
							when().
							get(countUrl + "/count").as(CountWrapper.class);

			CountWrapper result =
					given().
							pathParams("delta", delta).
							when().
							put(countUrl + "/count/plus/{delta}").as(CountWrapper.class);

			System.out.println(Thread.currentThread().getName() + "  -----  " + result.getCount() + "   PUT request");

			Assert.notNull(result, "result is null");
			Assert.notNull(result.getTimestamp(), "timeStamp is empty");
			Assert.isTrue(result.getCount() - before.getCount() == delta, "wrong result");
		return result;
		}
	}

	static class GetRequest implements Callable<CountWrapper> {
		@Override
		public CountWrapper call() throws Exception {
			CountWrapper result =
					given().
							when().
							get(countUrl + "/count").as(CountWrapper.class);

			System.out.println(Thread.currentThread().getName() + "  -----  " + result.getCount() + "   GET request");
			Assert.notNull(result, "result is null");
			Assert.notNull(result.getTimestamp(), "timeStamp is empty");
			return result;
		}
	}

	static class PostRequest implements Callable<CountWrapper>{
		@Override
		public CountWrapper call() throws Exception {
			WhoModel model = new WhoModel();
			model.setWho("Bobby");
			model.setCount(256);

			CountWrapper before =
					given().
							when().
							get(countUrl + "/count").as(CountWrapper.class);
			Assert.notNull(before, "result is null");
			Assert.notNull(before.getTimestamp(), "timeStamp is empty");

			CountWrapper result =
					given().
							contentType("application/json").
							body(model).
							when().
							post(countUrl + "/count").as(CountWrapper.class);

			Assert.notNull(result, "result is null");
			Assert.notNull(result.getTimestamp(), "timeStamp is empty");
			Assert.isTrue(result.getCount() == model.getCount(), "wrong result");

			CountWrapper testResult =
					given().
							when().
							get(countUrl + "/count").as(CountWrapper.class);

			System.out.println(Thread.currentThread().getName() + "  -----  " + result.getCount() + "   POST request");
			Assert.notNull(testResult, "result is null");
			Assert.notNull(testResult.getTimestamp(), "timeStamp is empty");
			Assert.isTrue(result.getCount() == testResult.getCount(), "wrong result");
		return result;
		}
	}
}