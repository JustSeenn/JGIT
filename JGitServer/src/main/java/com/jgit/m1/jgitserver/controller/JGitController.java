package com.jgit.m1.jgitserver.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jgit.m1.jgitserver.model.PushRequest;
import com.jgit.m1.jgitserver.util.Util;

/**
 * @author fayss
 *
 */
@RestController
public class JGitController {

	@PostMapping(value = "/push")
	public void push(@RequestBody PushRequest pushRequest) throws IOException {
		pushRequest.storePushRequest();
	}

	@PostMapping(value = "/post")
	public void post(@RequestBody String requestString) {
		// ...
	}

	@PostMapping(value = "/clean")
	public String clean() {
		Util.deleteFolder("./.jgit");
		return ".jgit Folder deleted";
	}

	@PostMapping(value = "/")
	public String home(@RequestBody String requestString) {
		return requestString;
	}
}
