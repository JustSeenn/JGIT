package fr.uca.jgit.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fr.uca.jgit.model.PushRequest;
import fr.uca.jgit.util.Util;

/**
 * @author fayssk
 *
 */
@RestController
public class ServerController {

	@PostMapping(value = "/push")
	public void push(@RequestBody PushRequest pushRequest) throws IOException {
		Util.deleteFolder("./.jgitserver"); // to be changed
		pushRequest.storePushRequest();
	}

	@PostMapping(value = "/pull")
	public void pull(@RequestBody String requestString) {
		// ...
	}

	@PostMapping(value = "/clean")
	public String clean() {
		Util.deleteFolder("./.jgitserver");
		return ".jgit Folder deleted";
	}

	@PostMapping(value = "/")
	public String home(@RequestBody String requestString) {
		return requestString;
	}
}
