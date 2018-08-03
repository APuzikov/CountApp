package ru.mera.count.controller;

import org.junit.Test;
import org.springframework.util.Assert;
import ru.mera.count.model.CountWrapper;
import ru.mera.count.model.StorageModel;
import ru.mera.count.model.WhoModel;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.form;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

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
	public void test_setStat(){
		ExecutorService executorService = Executors.newFixedThreadPool(100);

		for (int i = 0; i < 100; i++) {
			Future<CountWrapper> result = executorService.submit(new PutRequest());
			try {
				System.out.println(result.get().getCount());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		try {
			Thread.sleep(1000);
			System.out.println("Sleep 1 sec ---------------------------------");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 100; i++){
			Future<CountWrapper> result = executorService.submit(new GetRequest());
			try {
				System.out.println(result.get().getCount());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		try {
			Thread.sleep(1000);
			System.out.println("Sleep 1 sec ---------------------------------");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 100; i++){
			Future<CountWrapper> result = executorService.submit(new PostRequest());
			try {
				System.out.println(result.get().getCount());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		executorService.shutdown();
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

			Assert.notNull(testResult, "result is null");
			Assert.notNull(testResult.getTimestamp(), "timeStamp is empty");
			Assert.isTrue(result.getCount() == testResult.getCount(), "wrong result");
		return result;
		}
	}
}