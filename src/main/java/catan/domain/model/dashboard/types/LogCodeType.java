package catan.domain.model.dashboard.types;

public enum LogCodeType {
    START_GAME("log_msg_start_game"),
    THROW_DICE("log_msg_throw_dice"),
    END_TURN("log_msg_end_turn"),
    FINISH_GAME("log_msg_finish_game"),
    BUILD_SETTLEMENT("log_msg_build_settlement"),
    BUILD_CITY("log_msg_build_city"),
    BUILD_ROAD("log_msg_build_road"),
    BUY_CARD("log_msg_buy_card"),
    NEW_WIDEST_NETWORK("log_msg_new_widest_network"),
    INTERRUPTED_WIDEST_NETWORK("log_msg_interrupted_widest_network"),
    NEW_SECURITY_LEADER("log_msg_new_security_leader"),
    ROB_PLAYER("log_msg_rob_player"),
    MOVE_ROBBER("log_msg_move_robber"),
    STEAL_RESOURCE("log_msg_steal_resource"),
    USE_CARD_KNIGHT("log_msg_use_card_knight"),
    USE_CARD_ROAD_BUILDING("log_msg_use_card_road_building"),
    USE_CARD_MONOPOLY("log_msg_use_card_monopoly"),
    USE_CARD_YEAR_OF_PLENTY("log_msg_use_card_year_of_plenty"),
    TRADE_PORT("log_msg_trade_port"),
    TRADE_PROPOSE("log_msg_trade_propose"),
    TRADE_ACCEPT("log_msg_trade_accept"),
    TRADE_DECLINE("log_msg_trade_decline");

    private final String logMsgPatternName;

    LogCodeType(String logMsgPatternName) {
        this.logMsgPatternName = logMsgPatternName;
    }

    public String getLogMsgPatternName() {
        return this.logMsgPatternName;
    }
}