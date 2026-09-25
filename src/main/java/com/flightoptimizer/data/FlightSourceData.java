package com.flightoptimizer.data;

import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.SearchQuery;

import java.util.List;

public interface FlightSourceData {
    List<FlightSegment> findAllCandidates(SearchQuery query);
}
