package io.geewit.weixin.event.mp.webmvc.context;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Builder
@Getter
public class MpEvent extends ApplicationEvent {

    private EventContext context;

    public MpEvent(EventContext context) {
        super(context);
    }
}
