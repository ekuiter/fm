/*
 * Copyright (C) 2024 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-feature-model.
 *
 * feature-model is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * feature-model is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with feature-model. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-feature-model> for further information.
 */
package de.featjar.feature.model;

import static org.junit.jupiter.api.Assertions.*;

import de.featjar.base.data.Result;
import de.featjar.base.data.identifier.IIdentifier;
import de.featjar.base.data.identifier.Identifiers;
import de.featjar.formula.structure.Expressions;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link FeatureModel}, its elements, and its mixins.
 *
 * @author Elias Kuiter
 */
public class FeatureModelTest {
    IFeatureModel featureModel;

    @BeforeEach
    public void createFeatureModel() {
        featureModel = new FeatureModel(Identifiers.newCounterIdentifier());
    }

    

    //kind of internal-decision making controlled mechanism whether root feature should be deleted.
    
    private static class TestableFeatureModel extends FeatureModel {
        private boolean shouldDeleteRootFeatureFlag = true;

        public TestableFeatureModel(IIdentifier iIdentifier) {
            super();   //super(identifier)
        }
        
        public void setShouldDeleteRootFeatureFlag(boolean flag) {
            this.shouldDeleteRootFeatureFlag = flag;
        }

        @Override
        protected boolean shouldDeleteRootFeature(IFeature feature) {
            return this.shouldDeleteRootFeatureFlag;
        }
    }   
        
     //sarthak  
    
    
    
    @Test
    public void featureModel() {
        Assertions.assertEquals("1", featureModel.getIdentifier().toString());
        assertTrue(featureModel.getRoots().isEmpty());
        assertTrue(featureModel.getFeatures().isEmpty());
        assertTrue(featureModel.getConstraints().isEmpty());
    }

    @Test
    public void commonAttributesMixin() {
        Assertions.assertEquals("@1", featureModel.getName().get());
        Assertions.assertEquals(Result.empty(), featureModel.getDescription());
        featureModel.mutate().setName("My Model");
        featureModel.mutate().setDescription("awesome description");
        Assertions.assertEquals(Result.of("My Model"), featureModel.getName());
        Assertions.assertEquals(Result.of("awesome description"), featureModel.getDescription());
    }

    @Test
    public void featureModelConstraintMixin() {
        Assertions.assertEquals(0, featureModel.getNumberOfConstraints());
        IConstraint constraint1 = featureModel.mutate().addConstraint(Expressions.True);
        IConstraint constraint2 = featureModel.mutate().addConstraint(Expressions.True);
        IConstraint constraint3 = featureModel.mutate().addConstraint(Expressions.False);
        Assertions.assertEquals(3, featureModel.getNumberOfConstraints());
        Assertions.assertEquals(Result.of(constraint1), featureModel.getConstraint(constraint1.getIdentifier()));
        Assertions.assertTrue(featureModel.hasConstraint(constraint2.getIdentifier()));
        constraint2.mutate().remove();
        Assertions.assertFalse(featureModel.hasConstraint(constraint2.getIdentifier()));
        Assertions.assertTrue(featureModel.hasConstraint(constraint3));
    }

    @Test
    public void featureModelFeatureTreeMixin() {
        IFeature rootFeature = featureModel.mutate().addFeature("root");
        Assertions.assertEquals(1, featureModel.getNumberOfFeatures());
        IFeatureTree rootTree = featureModel.mutate().addFeatureTreeRoot(rootFeature);
        IFeature childFeature = featureModel.mutate().addFeature("child1");
        final IFeatureTree childTree = rootTree.mutate().addFeatureBelow(childFeature);
        assertSame(childFeature, childTree.getFeature());
        assertSame(childTree, childFeature.getFeatureTree().get());
        assertSame(childFeature, childTree.getFeature());
        assertSame(rootFeature, childTree.getParent().get().getFeature());
        assertSame(childTree.getParent().get(), rootFeature.getFeatureTree().get());
        assertSame(featureModel.getFeature(childFeature.getIdentifier()).get(), childFeature);
        Assertions.assertEquals(2, featureModel.getNumberOfFeatures());
        Assertions.assertEquals(Result.of(childFeature), featureModel.getFeature(childFeature.getIdentifier()));
        Assertions.assertTrue(featureModel.hasFeature(childFeature.getIdentifier()));
        Assertions.assertTrue(featureModel.getFeature("root2").isEmpty());
        rootFeature.mutate().setName("root2");
        Assertions.assertEquals(Result.of(rootFeature), featureModel.getFeature("root2"));
        assertEquals(List.of(childTree), rootFeature.getFeatureTree().get().getChildren());
        assertEquals(rootFeature.getFeatureTree(), childTree.getParent());
        childTree.mutate().removeFromTree();
        assertEquals(List.of(), rootFeature.getFeatureTree().get().getChildren());
    }
    
 //sarthak
    

    @Test
    public void testDeleteRootFeatureAndPromoteChildren() {
    	
    	// Create a TestableFeatureModel instance and cast featureModel to it
        featureModel = new TestableFeatureModel(Identifiers.newCounterIdentifier());
        TestableFeatureModel testableFeatureModel = (TestableFeatureModel) featureModel;
    	
    	
    	
        // Setup the initial tree structure with a simple hierarchy
        IFeature rootFeature = testableFeatureModel.mutate().addFeature("root");
        IFeature childFeature1 = testableFeatureModel.mutate().addFeature("child1");
        IFeature childFeature2 = testableFeatureModel.mutate().addFeature("child2");
        IFeature childFeature3 = testableFeatureModel.mutate().addFeature("child3");
        IFeature childFeature4 = testableFeatureModel.mutate().addFeature("child4");

        testableFeatureModel.mutate().addFeatureTreeRoot(rootFeature)
            .mutate().addFeatureBelow(childFeature1)
            .mutate().addFeatureBelow(childFeature2)
            .mutate().addFeatureBelow(childFeature3)
            .mutate().addFeatureBelow(childFeature4);
        
        
        // Add grandchildren
        IFeature childFeature1a = testableFeatureModel.mutate().addFeature("a");
        IFeature childFeature1b = testableFeatureModel.mutate().addFeature("b");
        IFeature childFeature2c = testableFeatureModel.mutate().addFeature("c");
        IFeature childFeature3d = testableFeatureModel.mutate().addFeature("d");
        IFeature childFeature3e = testableFeatureModel.mutate().addFeature("e");
        IFeature childFeature4f = testableFeatureModel.mutate().addFeature("f");

        childFeature1.getFeatureTree().get().mutate().addFeatureBelow(childFeature1a).mutate().addFeatureBelow(childFeature1b);
        childFeature2.getFeatureTree().get().mutate().addFeatureBelow(childFeature2c);
        childFeature3.getFeatureTree().get().mutate().addFeatureBelow(childFeature3d).mutate().addFeatureBelow(childFeature3e);
        childFeature4.getFeatureTree().get().mutate().addFeatureBelow(childFeature4f);
        
        
        // Execute the deletion of the root feature
        testableFeatureModel.deleteFeatureAndPromoteChildren(rootFeature.getIdentifier());

        // Assert that all original children of the root are now root features
        Assertions.assertTrue(testableFeatureModel.getRoots().containsAll(Arrays.asList(childFeature1, childFeature2, childFeature3, childFeature4)));
        Assertions.assertFalse(testableFeatureModel.getFeatures().contains(rootFeature));
        
     // Assert the structure of subtrees remains intact under the new roots
    	Assertions.assertEquals(2, childFeature1.getFeatureTree().get().getChildren().size());
    	Assertions.assertTrue(childFeature1.getFeatureTree().get().getChildren().containsAll(Arrays.asList(childFeature1a.getFeatureTree().get(), childFeature1b.getFeatureTree().get())));
    	Assertions.assertTrue(childFeature2.getFeatureTree().get().getChildren().contains(childFeature2c.getFeatureTree().get()));
    	Assertions.assertEquals(2, childFeature3.getFeatureTree().get().getChildren().size());
    	Assertions.assertTrue(childFeature3.getFeatureTree().get().getChildren().containsAll(Arrays.asList(childFeature3d.getFeatureTree().get(), childFeature3e.getFeatureTree().get())));
    	Assertions.assertTrue(childFeature4.getFeatureTree().get().getChildren().contains(childFeature4f.getFeatureTree().get()));

    }
    
    	
    // this test checks if root feature is still present and its children are intact
    
    
    @Test
    public void testKeepRootFeatureAndChildrenIntact() {
    	
    	// Create a TestableFeatureModel instance and cast featureModel to it
        featureModel = new TestableFeatureModel(Identifiers.newCounterIdentifier());
        TestableFeatureModel testableFeatureModel = (TestableFeatureModel) featureModel;

    	
        // Setup the initial tree structure with a simple hierarchy
        IFeature rootFeature = testableFeatureModel.mutate().addFeature("root");
        IFeature childFeature1 = testableFeatureModel.mutate().addFeature("child1");
        IFeature childFeature2 = testableFeatureModel.mutate().addFeature("child2");
        IFeature childFeature3 = testableFeatureModel.mutate().addFeature("child3");
        IFeature childFeature4 = testableFeatureModel.mutate().addFeature("child4");

        IFeatureModel tetsableFeatureModel;								//sarthak
		tetsableFeatureModel.mutate().addFeatureTreeRoot(rootFeature)
            .mutate().addFeatureBelow(childFeature1)
            .mutate().addFeatureBelow(childFeature2)
            .mutate().addFeatureBelow(childFeature3)
            .mutate().addFeatureBelow(childFeature4);
        
        
     // Add grandchildren
        IFeature childFeature1a = testableFeatureModel.mutate().addFeature("a");
        IFeature childFeature1b = testableFeatureModel.mutate().addFeature("b");
        IFeature childFeature2c = testableFeatureModel.mutate().addFeature("c");
        IFeature childFeature3d = testableFeatureModel.mutate().addFeature("d");
        IFeature childFeature3e = testableFeatureModel.mutate().addFeature("e");
        IFeature childFeature4f = testableFeatureModel.mutate().addFeature("f");

        childFeature1.getFeatureTree().get().mutate().addFeatureBelow(childFeature1a).mutate().addFeatureBelow(childFeature1b);
        childFeature2.getFeatureTree().get().mutate().addFeatureBelow(childFeature2c);
        childFeature3.getFeatureTree().get().mutate().addFeatureBelow(childFeature3d).mutate().addFeatureBelow(childFeature3e);
        childFeature4.getFeatureTree().get().mutate().addFeatureBelow(childFeature4f);


        // Set the flag to prevent root feature deletion
        testableFeatureModel.setShouldDeleteRootFeatureFlag(false);

        // Execute the deletion of the root feature
        testableFeatureModel.deleteFeatureAndPromoteChildren(rootFeature.getIdentifier());

        // Assert that the root feature is still present
        Assertions.assertTrue(testableFeatureModel.getFeatures().contains(rootFeature));

        // Assert that the children are still correctly below the root
        Assertions.assertTrue(rootFeature.getFeatureTree().get().getChildren().containsAll(Arrays.asList(
                childFeature1.getFeatureTree().get(), childFeature2.getFeatureTree().get(), 
                childFeature3.getFeatureTree().get(), childFeature4.getFeatureTree().get())));
        
        // Assert the structure of subtrees remains intact
        Assertions.assertEquals(2, childFeature1.getFeatureTree().get().getChildren().size());
        Assertions.assertTrue(childFeature1.getFeatureTree().get().getChildren().containsAll(Arrays.asList(childFeature1a.getFeatureTree().get(), childFeature1b.getFeatureTree().get())));
        Assertions.assertTrue(childFeature2.getFeatureTree().get().getChildren().contains(childFeature2c.getFeatureTree().get()));
        Assertions.assertEquals(2, childFeature3.getFeatureTree().get().getChildren().size());
        Assertions.assertTrue(childFeature3.getFeatureTree().get().getChildren().containsAll(Arrays.asList(childFeature3d.getFeatureTree().get(), childFeature3e.getFeatureTree().get())));
        Assertions.assertTrue(childFeature4.getFeatureTree().get().getChildren().contains(childFeature4f.getFeatureTree().get()));
    
        
    }

}

	//sarthak


