/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.superbiz.dynamicdatasourcerouting;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import entities.User_roles;
import entities.Users;

@Stateless
public class RoutedPersister {

	@PersistenceContext(unitName = "router")
	private EntityManager em;

	@Resource(name = "My_Router", type = DeterminedRouter.class)
	private DeterminedRouter router;
	private static final Logger log = Logger.getLogger(RoutedPersister.class);
	
	/**
	 * 
	 * Persist new User with specific Role setted by interface parameters
	 * 
	 * @param username
	 * @param password
	 * @param user_role
	 * @throws Exception
	 */
	public void persist(String username, String password, String user_role) throws Exception {

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		log.info("TENANT: " + (String) session.getAttribute("tenant") + " PASSWORD: " + password);
		router.setDataSource((String) session.getAttribute("tenant"));

		em.persist(new Users(username, password, (String) session.getAttribute("tenant")));
		em.persist(new User_roles(username, user_role));

	}
}
