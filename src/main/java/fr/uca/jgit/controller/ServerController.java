package fr.uca.jgit.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fr.uca.jgit.model.PullRequest;
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

	@GetMapping()
	public ResponseEntity<PullRequest> pull() {
		PullRequest pullRequest = new PullRequest();
		try {
			pullRequest.loadPullRequest();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(pullRequest);
	}

	@PostMapping(value = "/clean")
	public String clean() {
		Util.deleteFolder("./.jgitserver");
		return ".jgitserver Folder deleted";
	}

	@PostMapping(value = "/")
	public String home(@RequestBody String requestString) {
		return requestString;
	}
}
