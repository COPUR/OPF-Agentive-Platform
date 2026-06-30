package com.xbank.opf.party.domain.port.out;

import java.util.Optional;

public interface PartyDataPort {
    Optional<Object> findById(String id);
}
