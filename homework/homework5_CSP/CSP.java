package csp;

import java.util.*;
import java.time.LocalDate;
import java.util.stream.*;

/**
 * CSP: Calendar Satisfaction Problem Solver Provides a solution for scheduling
 * some n meetings in a given period of time and according to some set of unary
 * and binary constraints on the dates of each meeting.
 */
public class CSP {

    /**
     * Creates a set of meetings based on the nMeetings, creates Meeting objects
     * based on the passed in LocalDate ranges and set of constraints.
     * 
     * @param int                 nMeetings
     * @param LocalDate           rangeStart
     * @param LocalDate           rangeEnd
     * @param Set<DateConstraint> constraints
     * @return Set<Meeting>
     */
    private static ArrayList<Meeting> createMeetings(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd,
            Set<DateConstraint> constraints) {
        Queue<Meeting> temp = new LinkedList<Meeting>();
        for (int i = 0; i < nMeetings; i++) {
            Meeting m = new Meeting(i, rangeStart, rangeEnd, constraints);
            temp.add(m);
        }
        ArrayList<Meeting> meetings = new ArrayList<Meeting>(temp);
        return meetings;
    }

    /**
     * Checks if a two local dates are consistent based on the constraint passed.
     * Returns true if the given m is inconsistent.
     * 
     * @param Meeting   m
     * @param LocalDate rdate
     * @param String    constraint
     * @return boolean true / false
     */
    private static boolean isInconsistent(LocalDate left, LocalDate right, String op) {
        boolean violated = true;
        switch (op) {
        case "==":
            violated = (left.isEqual(right)) ? false : true;
            left = right;
            break;
        case "!=":
            violated = (!left.isEqual(right)) ? false : true;
            break;
        case "<":
            violated = (left.isBefore(right)) ? false : true;
            break;
        case "<=":
            violated = (left.isBefore(right) || left.isEqual(right)) ? false : true;
            break;
        case ">":
            violated = (left.isAfter(right)) ? false : true;
            break;
        case ">=":
            violated = (left.isAfter(right) || left.isEqual(right)) ? false : true;
            break;
        }
        return violated;
    }

    /**
     * Checks if an entire assignment is consistent according to its constraints.
     * 
     * @param List<LocalDate>      solution
     * @param Set<DateConstraints> constraints
     * @param List<Meeting>        meetings
     * @return boolean
     */
    private static boolean isConsistentAssignment(List<LocalDate> solution, Set<DateConstraint> constraints,
            List<Meeting> meetings) {
        BinaryDateConstraint tempBinary = null;
        LocalDate left;
        LocalDate right;
        for (DateConstraint dc : constraints) {
            if (dc.arity() == 2) {
                tempBinary = (BinaryDateConstraint) dc;
                left = meetings.get(dc.L_VAL).currentAssignment;
                right = meetings.get(tempBinary.R_VAL).currentAssignment;
                if (left != null && right != null) {
                    if (isInconsistent(left, right, dc.OP)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if the set of arcs contains a particular arc, necessary since the set
     * can ensure int[][] uniqueness.
     * 
     * @param int[] arc
     * @param Queue <int[]> arcs
     * @return boolean
     */
    private static boolean arcsetContains(int[] arc, Queue<int[]> arcs) {
        for (int[] a : arcs) {
            if ((a[0] == arc[0]) && (a[1] == arc[1])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Make a set of arcs for the given m. Also makes inverted arcs if needed.
     * 
     * @param Meeting      m
     * @param boolean      invert
     * @param Queue<int[]> oldArcs, needed if inverting
     * @return Queue<int[]> arcs
     */
    private static Queue<int[]> makeArcs(Meeting m, boolean invert, Queue<int[]> oldArcs) {
        Queue<int[]> arcsForMeeting = new LinkedList<int[]>();
        int[] arc;
        BinaryDateConstraint bc = null;
        if (invert == false) {
            for (DateConstraint dc : m.constraints) {
                if (dc.arity() == 2) {
                    bc = (BinaryDateConstraint) dc;
                    if (bc.L_VAL == m.meetingIndex) {
                        arc = new int[2];
                        arc[0] = bc.L_VAL;
                        arc[1] = bc.R_VAL;
                    } else {
                        arc = new int[2];
                        arc[0] = bc.R_VAL;
                        arc[1] = bc.L_VAL;
                    }
                    if (!arcsetContains(arc, arcsForMeeting)) {
                        arcsForMeeting.add(arc);
                    }
                }
            }
        } else {
            arcsForMeeting.addAll(oldArcs);
            arc = new int[2];
            int left = 0;
            int right = 0;
            for (int[] oldArc : oldArcs) {
                if (m.meetingIndex == oldArc[0]) {
                    arc = new int[2];
                    left = oldArc[0];
                    right = oldArc[1];
                    arc[0] = right;
                    arc[1] = left;
                    if (!arcsetContains(arc, arcsForMeeting)) {
                        arcsForMeeting.add(arc);
                    }
                }
            }
        }
        return arcsForMeeting;
    }

    /**
     * Prunes out domains according to the unary constraints for each meeting.
     * 
     * @param List<Meeting> meetings
     */
    private static void nodeProcessing(List<Meeting> meetings) {
        LocalDate right;
        UnaryDateConstraint tempUnary = null;
        Set<LocalDate> toRemove = new HashSet<LocalDate>();
        for (Meeting m : meetings) {
            for (DateConstraint dc : m.constraints) {
                if (dc.arity() == 1) {
                    tempUnary = (UnaryDateConstraint) dc;
                    right = tempUnary.R_VAL;
                    for (LocalDate d : m.domain) {
                        if (isInconsistent(d, right, dc.OP)) {
                            toRemove.add(d);
                        }
                    }

                }
            }
            m.domain.removeAll(toRemove);
        }
    }

    /**
     * Prunes our domains according to the binary constraints for each meeting.
     * 
     * @param meetings
     * @param constraints
     */
    private static void arcProcessing(List<Meeting> meetings, Set<DateConstraint> constraints) {
        Queue<int[]> arcs = new LinkedList<int[]>();
        for (Meeting m : meetings) {
            arcs.addAll(makeArcs(m, false, arcs));
        }
        Set<LocalDate> toRemove = new HashSet<LocalDate>();
        int[] current;
        Meeting left;
        Meeting right;
        BinaryDateConstraint bc = null;
        int consistentCount = 0;

        while (!arcs.isEmpty()) {
            current = arcs.poll();
            left = meetings.get(current[0]);
            right = meetings.get(current[1]);
            if (!left.constraints.isEmpty()) {
                for (DateConstraint leftConstraint : left.constraints) {
                    for (LocalDate leftDate : left.domain) {
                        if (leftConstraint.arity() == 2) {
                            bc = (BinaryDateConstraint) leftConstraint;
                            for (LocalDate rightDate : right.domain) {
                                if ((leftConstraint.L_VAL == current[0]) && (bc.R_VAL == current[1])) {
                                    if (!isInconsistent(leftDate, rightDate, leftConstraint.OP)) {   
                                        consistentCount++;
                                    }
                                }
                                else if ((leftConstraint.L_VAL == current[1]) && (bc.R_VAL == current[0])) {
                                    if (!isInconsistent(rightDate, leftDate, leftConstraint.OP)) {   
                                        consistentCount++;
                                    }
                                }
                            }
                        }
                        if (consistentCount == 0) {
                            toRemove.add(leftDate);
                        }
                    }
                }
                if (!toRemove.isEmpty()) {
                    left.domain.removeAll(toRemove);
                    arcs = makeArcs(left, true, arcs);
                    toRemove = new HashSet<LocalDate>();
                }
            }
        }
    }

    /**
     * Runs through the AC-3 algorithm to preprocess domains before backtracking.
     * 
     * @param int                  nMeeting
     * @param LocalDate            rangeStart
     * @param LocalDate            rangeEnd
     * @param Set<DateConstraints> constraints
     * @param Set<Meetings>        meetings
     */
    private static void preprocessing(List<Meeting> meetings, Set<DateConstraint> constraints) {
        nodeProcessing(meetings);
        arcProcessing(meetings, constraints);
    }

    /**
     * Performs backtracking for the set of all meetings. Will handle meeting
     * assignments at this point.
     * 
     * @param meetings
     */
    private static List<LocalDate> backtrack(int nMeeting, List<LocalDate> assignments, Set<DateConstraint> constraints,
            List<Meeting> meetings, int index) {
        LocalDate currentDate;
        if (index == nMeeting && isConsistentAssignment(assignments, constraints, meetings)) {
            return assignments;
        }
        Meeting current = meetings.get(index);
        for (int j = 0; j < current.domain.size(); j++) {
            currentDate = current.domain.get(j);
            if (currentDate != null) {
                assignments.add(currentDate);
                current.currentAssignment = currentDate;
            }
            if (isConsistentAssignment(assignments, constraints, meetings)) {
                List<LocalDate> complete = backtrack(nMeeting, assignments, constraints, meetings, index + 1);
                if (complete != null) {
                    return complete;
                }
            }
            current.currentAssignment = null;
            if (assignments.contains(currentDate) && assignments.get(index).isEqual(currentDate)) {
                assignments.remove(index);
            }
        }
        return null;
    }

    /**
     * Public interface for the CSP solver in which the number of meetings, range of
     * allowable dates for each meeting, and constraints on meeting times are
     * specified.
     * 
     * @param nMeetings   The number of meetings that must be scheduled, indexed
     *                    from 0 to n-1
     * @param rangeStart  The start date (inclusive) of the domains of each of the n
     *                    meeting-variables
     * @param rangeEnd    The end date (inclusive) of the domains of each of the n
     *                    meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary
     *                    for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of
     *         the n meetings, indexed by the variable they satisfy, or null if no
     *         solution exists.
     */
    public static List<LocalDate> solve(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd,
            Set<DateConstraint> constraints) {
        ArrayList<Meeting> meetings = createMeetings(nMeetings, rangeStart, rangeEnd, constraints);
        preprocessing(meetings, constraints);
        List<LocalDate> solution = new ArrayList<LocalDate>();
        return backtrack(nMeetings, solution, constraints, meetings, 0);
    }

    /**
     * Private static Meeting class that record keeps all relevant information
     * needed for backtracking. Will help with consistency checks and domain pruning
     * as we backtrack and preprocess for scheduling a set of meaning.
     */
    private static class Meeting {

        private int meetingIndex;
        private LocalDate currentAssignment;
        private List<LocalDate> domain;
        private Set<DateConstraint> constraints;

        Meeting(int index, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {
            this.currentAssignment = null;
            this.meetingIndex = index;
            this.domain = createDateDomains(rangeStart, rangeEnd);
            filterConstraints(constraints);
        }

        /**
         * Creates a set of dates based on the given ranges. To be used for
         * instantiation of the Meeting object later.
         * 
         * @param LocalDate rangeStart
         * @param LocalDate rangeEnd
         * @return Set<LocalDate>
         */
        private static List<LocalDate> createDateDomains(LocalDate rangeStart, LocalDate rangeEnd) {
            Set<LocalDate> temp = rangeStart.datesUntil(rangeEnd).collect(Collectors.toSet());
            ArrayList<LocalDate> dates = new ArrayList<LocalDate>();
            dates.addAll(temp);
            dates.add(rangeEnd);
            return dates;
        }

        /**
         * Filters all constraints into one set of class constraints for the given
         * meeting.
         * 
         * @param Set<DateConstraints>constraints
         */
        private void filterConstraints(Set<DateConstraint> constraints) {
            BinaryDateConstraint bc;
            this.constraints = new HashSet<DateConstraint>();

            for (DateConstraint constraint : constraints) {
                if (constraint.L_VAL == this.meetingIndex) {
                    this.constraints.add(constraint);
                }
                if (constraint.arity() == 2) {
                    bc = (BinaryDateConstraint) constraint;
                    if (bc.L_VAL == this.meetingIndex || bc.R_VAL == this.meetingIndex) {
                        this.constraints.add(constraint);
                    }
                }
            }
        }
    }
}