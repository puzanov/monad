package se.uu.csproject.monadclient.storage;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FullTrip implements Parcelable {
    private String id;                                              /* Same as the id of the first partial trip */
    private String travelRequestID;
    private String recommendationID;                                /* Same as the recommendation id in the database */
    private ArrayList<PartialTrip> partialTrips;
    private long duration;                                          /* Corresponds to minutes */
    private boolean reserved;                                       /* Initially false | true in case of reservation */
    private int feedback;                                           /* initially -1 | Possible values [-1, 5] */


    /*
     * Additional Methods
     * getID: Returns the id of the first partial trip
     * getStartBusStop: Returns the starting bus stop of the first partial trip
     * getEndBusStop: Returns the ending bus stop of the last partial trip
     * getStartTime: Returns the starting time of the first partial trip
     * getEndTime: Returns the ending time of the last partial trip
     * getDuration: Returns the duration of the full trip (ending time - start time) | Corresponds to minutes
     * getBusLines: Returns an ArrayList<int> containing the bus lines of the partial trips
     * getBusLinesString: Returns a String containing the bus lines of the partial trips
     * getBusIDs: Returns an ArrayList<String> containing the ids of the buses of the partial trips
     * getPartialTripByID: Returns a partial trip with specific id
     */

    public FullTrip(String id, String travelRequestID, ArrayList<PartialTrip> partialTrips, long duration,
                    boolean reserved, int feedback) {
        this.id = id;
        this.travelRequestID = travelRequestID;
        this.partialTrips = partialTrips;
        this.duration = duration;
        this.reserved = reserved;
        this.feedback = feedback;
    }

    public FullTrip(ArrayList<PartialTrip> partialTrips) {
        this.partialTrips = partialTrips;
        this.reserved = false;
        this.feedback = -1;
    }

    protected FullTrip(Parcel in) {
        id = in.readString();
        travelRequestID = in.readString();
        recommendationID = in.readString();
        if (in.readByte() == 0x01) {
            partialTrips = new ArrayList<>();
            in.readList(partialTrips, PartialTrip.class.getClassLoader());
        } else {
            partialTrips = null;
        }
        duration = in.readLong();
        reserved = in.readByte() != 0x00;
        feedback = in.readInt();
    }

    public String getId() {
        return partialTrips.get(0).getID();
    }

    public String getTravelRequestID(){
        return travelRequestID;
    }

    public void setTravelRequestID(String travelRequestID){
        this.travelRequestID = travelRequestID;
    }

    public String getRecommendationID() {
        return recommendationID;
    }

    public void setRecommendationID(String recommendationID) {
        this.recommendationID = recommendationID;
    }

    public ArrayList<PartialTrip> getPartialTrips() {
        return partialTrips;
    }

    public void setPartialTrips(ArrayList<PartialTrip> partialTrips) {
        this.partialTrips = partialTrips;
    }

    public PartialTrip getPartialTripByID(String partialTripID) {
        for (int i = 0; i < partialTrips.size(); i++) {
            if (partialTrips.get(i).getID().equals(partialTripID)) {
                return partialTrips.get(i);
            }
        }
        return null;
    }

    /* Returns duration in minutes */
    public long getDuration() {
        long startTime = partialTrips.get(0).getStartTime().getTime();
        long endTime = partialTrips.get(partialTrips.size() - 1).getEndTime().getTime();
        return TimeUnit.MILLISECONDS.toMinutes(endTime - startTime);
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }

    public String getStartBusStop() {
        return partialTrips.get(0).getStartBusStop();
    }

    public String getEndBusStop() {
        return partialTrips.get(partialTrips.size() - 1).getEndBusStop();
    }

    // returns time in Milliseconds
    public long getTimeToDeparture() {
        return partialTrips.get(0).getStartTime().getTime() - Calendar.getInstance().getTimeInMillis();
    }

    public Date getStartTime() {
        return partialTrips.get(0).getStartTime();
    }

    public Date getEndTime() {
        return partialTrips.get(partialTrips.size() - 1).getEndTime();
    }

    public ArrayList<Integer> getBusLines() {
        ArrayList<Integer> busLines = new ArrayList<>();
        for (int i = 0; i < partialTrips.size(); i++) {
            busLines.add(partialTrips.get(i).getLine());
        }
        return busLines;
    }

    public String getBusLinesString(){
        StringBuilder busLines = new StringBuilder("");
        for (int i = 0; i < partialTrips.size(); i++) {
            busLines.append(String.valueOf(partialTrips.get(i).getLine() + " "));
        }
        return busLines.toString();
    }

    // Determines if the trip is happening right now (true if: startTime < current time < endTime)
    public boolean isInProgress() {
        return partialTrips.get(0).getStartTime().before(Calendar.getInstance().getTime()) &&
                partialTrips.get(partialTrips.size() - 1).getEndTime().after(Calendar.getInstance().getTime());
    }

    // Determines if the trip has occurred already (true: endTime < current time)
    public boolean isHistory(){
        return partialTrips.get(partialTrips.size() - 1).getEndTime().before(Calendar.getInstance().getTime());
    }

    // Returns a boolean: true if day, month and year are all identical
    public boolean isToday() {
        Calendar today = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(partialTrips.get(0).getStartTime());
        return today.get(Calendar.DAY_OF_MONTH) == startDate.get(Calendar.DAY_OF_MONTH)
                && today.get(Calendar.MONTH) == startDate.get(Calendar.MONTH)
                && today.get(Calendar.YEAR) == startDate.get(Calendar.YEAR);
    }

    public void printValues() {
        Log.d("FullTrip", "-- Printing Values --");
        Log.d("FullTrip", "ID: " + getId());
        Log.d("FullTrip", "Request ID: " + getTravelRequestID());
        Log.d("FullTrip", "Recommendation ID: " + getRecommendationID());
        Log.d("FullTrip", "Reserved: " + isReserved());
        Log.d("FullTrip", "Duration: " + getDuration());
        Log.d("FullTrip", "Feedback: " + getFeedback());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(travelRequestID);
        dest.writeString(recommendationID);
        if (partialTrips == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(partialTrips);
        }
        dest.writeLong(duration);
        dest.writeByte((byte) (reserved ? 0x01 : 0x00));
        dest.writeInt(feedback);
    }

    public static final Parcelable.Creator<FullTrip> CREATOR = new Parcelable.Creator<FullTrip>() {
        @Override
        public FullTrip createFromParcel(Parcel in) {
            return new FullTrip(in);
        }

        @Override
        public FullTrip[] newArray(int size) {
            return new FullTrip[size];
        }
    };
}
