/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.calcium.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.objectweb.proactive.extensions.calcium.muscle.Condition;
import org.objectweb.proactive.extensions.calcium.muscle.Conquer;
import org.objectweb.proactive.extensions.calcium.muscle.Divide;
import org.objectweb.proactive.extensions.calcium.muscle.Execute;
import org.objectweb.proactive.extensions.calcium.muscle.Muscle;


public class Workout implements Serializable {
    public HashMap<Class<?>, Exercise> muscleWorkout;
    public static ClassSorterByName classSorterByName = new ClassSorterByName();

    public Workout(int initHashSize) {
        muscleWorkout = new HashMap<Class<?>, Exercise>();
    }

    @Override
    public String toString() {
        String workout = "Workout: ";

        List<Class<?>> keys = new ArrayList<Class<?>>(muscleWorkout.keySet());
        Collections.sort(keys, classSorterByName);

        for (Class<?> muscle : keys) {
            workout += (muscle.getSimpleName() + "(" + muscleWorkout.get(muscle) + ") ");
        }

        return workout;
    }

    //TODO this method should not be public
    public void track(Muscle muscle, Timer timer) {
        if (!muscleWorkout.containsKey(muscle.getClass())) {
            muscleWorkout.put(muscle.getClass(), new Exercise(muscle.getClass()));
        }

        Exercise workout = muscleWorkout.get(muscle.getClass());
        workout.incrementComputationTime(timer);
    }

    protected void track(Workout workout) {
        java.util.Iterator<Class<?>> it = workout.muscleWorkout.keySet().iterator();
        while (it.hasNext()) {
            Class<?> muscle = it.next();
            if (!this.muscleWorkout.containsKey(muscle)) {
                this.muscleWorkout.put(muscle, new Exercise(muscle.getClass()));
            }
            Exercise exercise = this.muscleWorkout.get(muscle);
            exercise.incrementComputationTime(workout.muscleWorkout.get(muscle));
        }
    }

    public Exercise getExercise(Muscle muscle) {
        return muscleWorkout.get(muscle.getClass());
    }

    /**
     * Looks inside the workout for classes that implement the requested interface.
     * @param search The interface used as pattern.
     * @return The Exercise found for the Classes that implement the interface.
     */
    private List<Exercise> getExercises(Class<?> search) {
        Vector<Exercise> v = new Vector<Exercise>();

        java.util.Iterator<Class<?>> it = muscleWorkout.keySet().iterator();
        while (it.hasNext()) {
            Class<?> muscle = it.next();
            Class<?>[] interfaces = muscle.getInterfaces();
            for (Class<?> c : interfaces) {
                if (c.equals(search)) {
                    v.add(muscleWorkout.get(muscle));
                }
            }
        }

        return v;
    }

    public List<Exercise> getConditionExercises() {
        return getExercises(Condition.class);
    }

    public List<Exercise> getDivideExercises() {
        return getExercises(Divide.class);
    }

    public List<Exercise> getConquerExercise() {
        return getExercises(Conquer.class);
    }

    public List<Exercise> getExecuteExercise() {
        return getExercises(Execute.class);
    }

    static class ClassSorterByName implements Comparator<Class<?>> {
        public int compare(Class<?> o1, Class<?> o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
