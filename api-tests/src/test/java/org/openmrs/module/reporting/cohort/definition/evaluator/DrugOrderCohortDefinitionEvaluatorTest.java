/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.DrugOrderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class DrugOrderCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return all patients taking any drugs (active or inactive)", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsTakingAnyDrugsActiveOrInactive() throws Exception {
		DrugOrderCohortDefinition cd = new DrugOrderCohortDefinition();
		cd.setOnlyCurrentlyActive(Boolean.FALSE);
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(3, c.size());
		Assert.assertTrue(c.contains(7));
		Assert.assertTrue(c.contains(2));
		Assert.assertTrue(c.contains(20));
	}

	/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return all patients taking Active drugs", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsTakingDrugsActive() throws Exception {
		DrugOrderCohortDefinition cd = new DrugOrderCohortDefinition();
		cd.setOnlyCurrentlyActive(Boolean.TRUE);
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(3, c.size());
		Assert.assertTrue(c.contains(7));
		Assert.assertTrue(c.contains(2));
		Assert.assertTrue(c.contains(20));
	}

	/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return all patients taking None of the drugs in a list", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsNotTakingAnyOfListedDrugs() throws Exception {
		DrugOrderCohortDefinition cd = new DrugOrderCohortDefinition();
		cd.setOnlyCurrentlyActive(Boolean.TRUE);
		List drugs = new ArrayList<Drug>();
		drugs.add(new Drug(10));
		drugs.add(new Drug(90));
		drugs.add(new Drug(7));
		cd.setDrugList(drugs);
		cd.setGroupMethod("NONE");
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(3, c.size());
		Assert.assertTrue(c.contains(7));
		Assert.assertTrue(c.contains(2));
		Assert.assertTrue(c.contains(20));
	}

	/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients taking Any of the drugs in a list", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsTakingAnyOfListedDrugs() throws Exception {
		DrugOrderCohortDefinition cd = new DrugOrderCohortDefinition();
		List drugs = new ArrayList<Drug>();
		drugs.add(new Drug(3));
		drugs.add(new Drug(2));
		cd.setDrugList(drugs);
		cd.setGroupMethod("ANY");
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(3, c.size());
		Assert.assertTrue(c.contains(7));
		Assert.assertTrue(c.contains(2));
		Assert.assertTrue(c.contains(20));
	}

	/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients taking All of the drugs in a list", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsTakingAllOfListedDrugs() throws Exception {
		DrugOrderCohortDefinition cd = new DrugOrderCohortDefinition();
		List drugs = new ArrayList<Drug>();
		drugs.add(new Drug(11));
		drugs.add(new Drug(3));
		cd.setDrugList(drugs);
		cd.setGroupMethod("ALL");
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, c.size());
		Assert.assertTrue(c.contains(7));
	}

	/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients taking Any drugs after a specific date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsTakingAllOfDrugsAfterSpecificDate() throws Exception {
		DrugOrderCohortDefinition cd = new DrugOrderCohortDefinition();
		cd.setActivatedOnOrAfter(DateUtil.getDateTime(2008, 8, 19));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(2, c.size());
		Assert.assertTrue(c.contains(7));
		Assert.assertTrue(c.contains(2));
	}

	/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients taking Any drugs before a specific date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsTakingAnuDrugBeforeSpecificDate() throws Exception {
		DrugOrderCohortDefinition cd = new DrugOrderCohortDefinition();
		cd.setActivatedOnOrBefore(DateUtil.getDateTime(2008, 8, 18));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(3, c.size());
		Assert.assertTrue(c.contains(7));
		Assert.assertTrue(c.contains(2));
		Assert.assertTrue(c.contains(20));
	}

		/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients who stopped taking Any drugs after a specific date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsWhoStoppedTakingAllOfDrugsAfterSpecificDate() throws Exception {
		DrugOrderCohortDefinition cd = new DrugOrderCohortDefinition();
		cd.setStoppedOnOrAfter(DateUtil.getDateTime(2008, 8, 07));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(2, c.size());
		Assert.assertTrue(c.contains(7));
		Assert.assertTrue(c.contains(2));
	}

	/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients who stopped taking Any drugs before a specific date", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsWhoStoppedTakingAnuDrugBeforeSpecificDate() throws Exception {
		DrugOrderCohortDefinition cd = new DrugOrderCohortDefinition();
		cd.setStoppedOnOrBefore(DateUtil.getDateTime(2007, 12, 25));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, c.size());
		Assert.assertTrue(c.contains(2));
	}

	/**
	 * @see {@link DrugOrderCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should return patients taking Any drugs within a date range", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsTakingAnyDrugWithinADateRange() throws Exception {
		DrugOrderCohortDefinition cd = new DrugOrderCohortDefinition();
		cd.setActivatedOnOrAfter(DateUtil.getDateTime(2007, 8, 18));
		cd.setStoppedOnOrBefore(DateUtil.getDateTime(2019, 8, 18));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(2, c.size());
		Assert.assertTrue(c.contains(2));
		Assert.assertTrue(c.contains(7));
	}

}