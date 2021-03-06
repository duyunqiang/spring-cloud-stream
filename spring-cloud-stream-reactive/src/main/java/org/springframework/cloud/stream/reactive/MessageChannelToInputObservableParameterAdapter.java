/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.reactive;

import reactor.adapter.RxJava1Adapter;
import rx.Observable;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.binding.StreamListenerParameterAdapter;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.util.Assert;

/**
 * Adapts an {@link org.springframework.cloud.stream.annotation.Input} annotated
 * {@link MessageChannel} to an {@link Observable}.
 * @author Marius Bogoevici
 */
public class MessageChannelToInputObservableParameterAdapter
		implements StreamListenerParameterAdapter<Observable<?>, SubscribableChannel> {

	private final MessageChannelToInputFluxParameterAdapter messageChannelToInputFluxArgumentAdapter;

	public MessageChannelToInputObservableParameterAdapter(
			MessageChannelToInputFluxParameterAdapter messageChannelToInputFluxArgumentAdapter) {
		Assert.notNull(messageChannelToInputFluxArgumentAdapter, "cannot be null");
		this.messageChannelToInputFluxArgumentAdapter = messageChannelToInputFluxArgumentAdapter;
	}

	public boolean supports(Class<?> boundElementType, MethodParameter methodParameter) {
		return SubscribableChannel.class.isAssignableFrom(boundElementType)
				&& methodParameter.getParameterAnnotation(Input.class) != null
				&& Observable.class.isAssignableFrom(methodParameter.getParameterType());
	}

	@Override
	public Observable<?> adapt(final SubscribableChannel boundElement, MethodParameter parameter) {
		return RxJava1Adapter.publisherToObservable(
				this.messageChannelToInputFluxArgumentAdapter.adapt(boundElement, parameter));
	}
}
