package io.eventuate.tram.spring.springwolf.core;

import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;

public interface EventuateTramChannelsScanner  {

  ElementsWithClasses<ChannelObject> scan();

}
