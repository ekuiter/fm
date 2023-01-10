/*
 * Copyright (C) 2023 Elias Kuiter
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
 * See <https://github.com/FeatureIDE/FeatJAR-model> for further information.
 */
package de.featjar.feature.model;

import de.featjar.base.data.Result;
import de.featjar.base.tree.structure.ITree;
import java.util.function.Predicate;

public interface IFeatureModelTree extends ITree<IFeatureModelTree> {
    IFeatureModel getFeatureModel();

    /**
     * {@return a validator that guarantees that the root of a child is a leaf in its parent}
     */
    @Override
    default Predicate<IFeatureModelTree> getChildValidator() {
        return featureModelTree -> {
            // todo
            Result<IFeature> featureInParent = getFeatureModel()
                    .getFeature(
                            featureModelTree.getFeatureModel().getRootFeature().getIdentifier());
            return featureInParent.isPresent()
                    && !featureInParent.get().getFeatureTree().hasChildren();
        };
    }
}
