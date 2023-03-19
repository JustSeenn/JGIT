//package fr.uca.jgit.controller;
//
//import java.io.IOException;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.jgit.m1.jgitserver.model.PushRequest;
//import com.jgit.m1.jgitserver.util.Util;
//
///**
// * @author fayss
// *
// */
//@RestController
//public class ServerController {
//
//	@PostMapping(value = "/push")
//	public void push(@RequestBody PushRequest pushRequest) throws IOException {
//		Util.deleteFolder("./.jgit"); // to be changed
//		pushRequest.storePushRequest();
//	}
//
//	@PostMapping(value = "/pull")
//	public void pull(@RequestBody String requestString) {
//		// ...
//	}
//
//	@PostMapping(value = "/clean")
//	public String clean() {
//		Util.deleteFolder("./.jgit");
//		return ".jgit Folder deleted";
//	}
//
//	@PostMapping(value = "/")
//	public String home(@RequestBody String requestString) {
//		return requestString;
//	}
//}
