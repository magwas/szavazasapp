package hu.kdea.szavazas.ballotprocessor.vote;

import java.util.List;

public record VoteMetadataData(String voteId, String voteName, int candidateCount, List<String> candidates, int supportColumnCount, List<String> issuedBallotIds) {
}
