package com.servicelibre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.manager.MotManager;


@Controller
public class MotController {

	@Autowired
	MotManager motManager;

	@RequestMapping(value = "/mot/tous", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody
	List<Mot> getMots() {

		List<Mot> mots = motManager.findAll();

		System.err.println("Trouvé " + mots.size() + " mots.");

		return mots;
	}
}