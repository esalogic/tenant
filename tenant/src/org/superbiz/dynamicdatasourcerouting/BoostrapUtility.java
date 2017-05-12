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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import entities.Users;

/**
 * OpenJPA create the table at the first query. To avoid to have to create the
 * table manually this singleton will do it for us.
 */
@Startup
@Singleton
public class BoostrapUtility {

	private static final Logger logger = Logger.getLogger(BoostrapUtility.class);

	@PersistenceContext(unitName = "router")
	private EntityManager em;

	@Resource(name = "My_Router", type = DeterminedRouter.class)
	private DeterminedRouter router;

	@PostConstruct
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void initDatabase() {
		for (String ds : router.getDataSourceNames().split(" ")) {
			logger.info("Create DDL for DS: " + ds);
			router.setDataSource(ds);
			em.find(Users.class, 0);
		}
	}
}
