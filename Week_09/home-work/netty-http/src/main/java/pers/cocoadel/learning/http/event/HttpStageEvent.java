package pers.cocoadel.learning.http.event;

public enum  HttpStageEvent {
    HTTP_STAGE_SENDING_EVENT(HttpStage.SENDING),
    HTTP_STAGE_WAITING_EVENT(HttpStage.WAIT),
    HTTP_STAGE_COMPLETED_EVENT(HttpStage.IDLE);
    private final HttpStage stage;

    HttpStageEvent(HttpStage stage) {
        this.stage = stage;
    }

    public HttpStage getStage() {
        return stage;
    }
}
