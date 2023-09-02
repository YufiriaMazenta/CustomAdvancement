package com.github.yufiriamazenta.customadvancement.criteria.trigger;

import org.bukkit.event.Event;

public interface ICriteria<E extends Event> {

    /**
     * 判断事件是否满足条件
     * @param event 传入的事件
     * @return 是否满足条件
     */
    boolean check(E event);

}
