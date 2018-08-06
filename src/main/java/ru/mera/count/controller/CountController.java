package ru.mera.count.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mera.count.model.Count;
import ru.mera.count.model.CountWrapper;
import ru.mera.count.model.Storage;
import ru.mera.count.model.StorageModel;
import ru.mera.count.model.WhoModel;
import ru.mera.count.util.Loggable;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("api/simple")
public class CountController extends Loggable{

	private Storage storage;

	@PostConstruct
	public void storageInit(){
		storage = new Storage();
	}

	@RequestMapping(path = "/count", method = RequestMethod.GET)
	public CountWrapper getCount(){
		storage.addCount("get");
		return new CountWrapper(Count.count, Instant.now());
	}

	@RequestMapping(path = "/count", method = RequestMethod.POST)
	public CountWrapper setCount(@RequestBody WhoModel who){
		logInfo(who.getWho() + " has set counter " + who.getCount());
		Count.count = who.getCount();
		storage.addCount("post");
		return new CountWrapper(Count.count, Instant.now());
	}

	@RequestMapping(path = "/count/plus/{delta}", method = RequestMethod.PUT)
	public CountWrapper increaseCount(@PathVariable(name = "delta") int delta){
		Count.count += delta;
		storage.addCount("put");
		return new CountWrapper(Count.count, Instant.now());
	}

	@RequestMapping(path = "/count/minus/{delta}", method = RequestMethod.PUT)
	public CountWrapper decreaseCount(@PathVariable(name = "delta") int delta){
		Count.count -= delta;
		storage.addCount("put");
		return new CountWrapper(Count.count, Instant.now());
	}

	@RequestMapping(path = "/count", method = RequestMethod.DELETE)
	public CountWrapper dropCount(){
		Count.count = 0;
		storage.addCount("delete");
		return new CountWrapper(Count.count, Instant.now());
	}

	@RequestMapping(path = "/stat", method = RequestMethod.GET)
	public StorageModel getStat(){

		storage.addCount("get");

		StorageModel model = new StorageModel();

		Map<String, Integer> requestCount = storage.getRequestCount();

		model.setGet(requestCount.get("get"));
		model.setPost(requestCount.get("post"));
		model.setPut(requestCount.get("put"));
		model.setDelete(requestCount.get("delete"));

		return model;
	}

	@RequestMapping(path = "/stat", method = RequestMethod.PUT)
	public StorageModel setStat(@RequestBody StorageModel body){
		storage.addCount("put");
		StorageModel model = new StorageModel();

		Map<String, Integer> requestCount = storage.getRequestCount();

		if (body.getGet() == null) {
			model.setGet(requestCount.get("get"));
		} else {
			model.setGet(body.getGet());
			storage.getRequestCount().put("get", body.getGet());
		}

		if (body.getPost() == null){
			model.setPost(requestCount.get("post"));
		} else {
			model.setPost(body.getPost());
			storage.getRequestCount().put("post", body.getPost());
		}

		if (body.getPut() == null){
			model.setPut(requestCount.get("put"));
		} else {
			model.setPut(body.getPut());
			storage.getRequestCount().put("put", body.getPut());
		}

		if (body.getDelete() == null){
			model.setDelete(requestCount.get("delete"));
		} else {
			model.setDelete(body.getDelete());
			storage.getRequestCount().put("delete", body.getDelete());
		}
		return model;
	}
}
