package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;

public interface TQFieldAssembler {
	Symbol toSymbol(TQSecID_F id);
	int toSecDisplayName(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	int toSecLotSize(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	int toSecTickSize(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	int toSecTickValue(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	int toSecInitialMargin(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	int toSecSettlementPrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	int toSecLowerPriceLimit(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	int toSecUpperPriceLimit(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	int toSecOpenPrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	int toSecHighPrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	int toSecLowPrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	int toSecClosePrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	int toSecExpirationTime(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
}
