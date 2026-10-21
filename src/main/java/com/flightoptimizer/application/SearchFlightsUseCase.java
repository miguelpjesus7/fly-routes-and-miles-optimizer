package com.flightoptimizer.application;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.flightoptimizer.data.OfferDataSource;
import com.flightoptimizer.domain.BudgetFilter;
import com.flightoptimizer.domain.FlightSegment;
import com.flightoptimizer.domain.Itinerary;
import com.flightoptimizer.domain.ConnectionValidator;
import com.flightoptimizer.domain.FlightOffer;
import com.flightoptimizer.domain.OfferEvaluator;
import com.flightoptimizer.domain.OfferRanker;
import com.flightoptimizer.domain.Recommendation;
import com.flightoptimizer.domain.RecommendationService;
import com.flightoptimizer.domain.SearchQuery;
import com.flightoptimizer.domain.SearchQueryValidator;
import com.flightoptimizer.graph.FlightGraph;
import com.flightoptimizer.search.FlightFinderViability;
import com.flightoptimizer.data.FlightSourceData;
import com.flightoptimizer.domain.MilesValuation;
import com.flightoptimizer.domain.OfferEvaluation;

public class SearchFlightsUseCase {

    private final SearchQueryValidator queryValidator;
    private final FlightSourceData flightSourceData;
    private final OfferDataSource offerDataSource;
    private final ConnectionValidator connectionValidator;
    private final OfferEvaluator offerEvaluator;
    private final BudgetFilter budgetFilter;
    private final OfferRanker offerRanker;
    private final RecommendationService recommendationService;
    private final Map<String, MilesValuation> valuationsByProgram;

    public SearchFlightsUseCase(
        SearchQueryValidator queryValidator,
        FlightSourceData flightSourceData,
        OfferDataSource offerDataSource,
        ConnectionValidator connectionValidator,
        OfferEvaluator offerEvaluator,
        BudgetFilter budgetFilter,
        OfferRanker offerRanker,
        RecommendationService recommendationService,
        Map<String, MilesValuation> valuationsByProgram
    ){
        if(queryValidator == null){
            throw new IllegalArgumentException("query validator must not be null");
        }
        if(flightSourceData == null){
            throw new IllegalArgumentException("flight data source must not be null");
        }
        if(offerDataSource == null){
            throw new IllegalArgumentException("offer data source must not be null");
        }
        if(connectionValidator == null){
            throw new IllegalArgumentException("connection validator must not be null");
        }
        if(offerEvaluator == null){
            throw new IllegalArgumentException("offer evaluator must not be null");
        }
        if(budgetFilter== null){
            throw new IllegalArgumentException("budget filter must not be null");
        }
        if(offerRanker== null){
            throw new IllegalArgumentException("offer ranker must not be null");
        }
        if(recommendationService == null){
            throw new IllegalArgumentException("recommendation service must not be null");
        }
        if(valuationsByProgram == null){
            throw new IllegalArgumentException("program miles valuations must not be null");
        }

        this.queryValidator = queryValidator;
        this.flightSourceData = flightSourceData;
        this.offerDataSource = offerDataSource;
        this.connectionValidator = connectionValidator;
        this.offerEvaluator = offerEvaluator;
        this.budgetFilter = budgetFilter;
        this.offerRanker = offerRanker;
        this.recommendationService = recommendationService;
        this.valuationsByProgram = Map.copyOf(valuationsByProgram);
    }

    public SearchResult execute(SearchQuery query){
        if(query == null){
            throw new IllegalArgumentException("query must not be null");
        }

        //validate query 
        queryValidator.validate(query);
        //find all candidates 
        List<FlightSegment> candidates = flightSourceData.findAllCandidates(query);
        //create a graph for all candidates
       FlightGraph candidateGraph = new FlightGraph();
        for(FlightSegment flight : candidates){
            candidateGraph.addFlight(flight);
        }
        //Find viable itineraries 
        FlightFinderViability finder = new FlightFinderViability(candidateGraph, connectionValidator);
        List<Itinerary> itineraries = finder.findAllRoutes(
            query.origin(), 
            query.destination(), 
            query.maximumSegments()
        );
        //Find offer of this itineraries 
        List<FlightOffer> offers = offerDataSource.findOffers(itineraries);
        List<OfferEvaluation> evaluations = new ArrayList<>();
        for(FlightOffer offer : offers){
            OfferEvaluation evaluation = offerEvaluator.evaluate(offer, valuationsByProgram);
            evaluations.add(evaluation);
        }
        //validate eligible offers 
        List<OfferEvaluation> eligibleOffers = budgetFilter.filter(evaluations, query.budget());
        //Rank offers
        List<OfferEvaluation> rankedOffers = offerRanker.rankByLowestCost(eligibleOffers);
        Optional<Recommendation> recommendation = recommendationService.recommend(rankedOffers);

        return new SearchResult(
            query, 
            rankedOffers, 
            recommendation
        );
    }
}
