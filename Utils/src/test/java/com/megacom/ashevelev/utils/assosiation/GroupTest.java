/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.megacom.ashevelev.utils.assosiation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author root
 */
public class GroupTest {

    TestElement element1g1;
    TestElement element2g1;

    TestElement element1g2;

    List<TestElement> elements;

    public GroupTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        element1g1 = new TestElement("arboc");
        element2g1 = new TestElement("arboc");

        element1g2 = new TestElement("maya");

        elements = new LinkedList();
        elements.add(element1g1);
        elements.add(element2g1);
        elements.add(element1g2);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMakeGroups() {
        System.out.println("==========GroupTest:testMakeGroups=============");
        Map<Object,Group<TestElement>> groups = Group.makeGroups(elements);
        System.out.println(groups);
        
        Group<TestElement> arboc = groups.get("arboc");
        Assert.assertTrue(arboc.elements.contains(element1g1));
        Assert.assertTrue(arboc.elements.contains(element2g1));
        
        Group<TestElement> maya = groups.get("maya");
        Assert.assertTrue(maya.elements.contains(element1g2));
    }

    @Data
    public static class TestElement implements GElement {

        protected String name;

        public TestElement(String name) {
            this.name = name;
        }

        @Override
        public Object getGroupSign() {
            return name;
        }

    }

}
