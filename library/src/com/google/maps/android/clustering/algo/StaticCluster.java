/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.clustering.algo;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A cluster whose center is determined upon creation.
 */
public class StaticCluster<T extends ClusterItem> implements Cluster<T> {
    private final LatLng mCenter;
    private final List<T> mItems = new ArrayList<T>();

    public StaticCluster(LatLng center) {
        mCenter = center;
    }

    public boolean add(T t) {
        return mItems.add(t);
    }

    @Override
    public LatLng getPosition() {
        return mCenter;
    }

    public boolean remove(T t) {
        return mItems.remove(t);
    }

    @Override
    public Collection<T> getItems() {
        return mItems;
    }

    @Override
    public int getSize() {
        return mItems.size();
    }

    private static boolean isEqual(double d0, double d1) {
        final double epsilon = 0.0000001;
        return Math.abs(d0 - d1) < epsilon;
    }

    private static boolean isEqualPosition(LatLng position0, LatLng position1) {
        return isEqual(position0.latitude, position1.latitude) &&
                isEqual(position0.longitude, position1.longitude);
    }

    @Override
    public String toString() {
        return "StaticCluster{" +
                "mCenter=" + mCenter +
                ", mItems.size=" + mItems.size() +
                '}';
    }

    @Override
    public boolean isOneLocation() {
        LatLng position = null;
        for (T item : mItems) {
            if (position == null || isEqualPosition(item.getPosition(), position)) {
                position = item.getPosition();
            } else {
                position = null;
                break;
            }
        }

        return position != null;
    }


    @Override
    public int hashCode() {
        return mCenter.hashCode() + mItems.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof StaticCluster<?>)) {
            return false;
        }

        return ((StaticCluster<?>) other).mCenter.equals(mCenter)
                && ((StaticCluster<?>) other).mItems.equals(mItems);
    }
}