/*
 * Copyright 2018 The MQTT Bee project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.mqttbee.util.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Iterator;
import java.util.NoSuchElementException;

@NotThreadSafe
public class ChunkedArrayQueue<E> implements Iterable<E> {

    private final int chunkSize;
    private Chunk<E> producerChunk;
    private Chunk<E> consumerChunk;
    private int producerIndex;
    private int consumerIndex;
    private int size;
    private final ChunkedArrayQueueIterator iterator = new ChunkedArrayQueueIterator();

    public ChunkedArrayQueue(final int chunkSize) {
        this.chunkSize = chunkSize;
        producerChunk = consumerChunk = new Chunk<>(chunkSize);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void offer(@NotNull final E e) {
        Chunk<E> producerChunk = this.producerChunk;
        int producerIndex = this.producerIndex;
        if ((producerIndex == chunkSize) ||
                ((producerChunk == consumerChunk) && (producerChunk.values[producerIndex] != null))) {
            if (size >= chunkSize) {
                final Chunk<E> chunk = new Chunk<>(chunkSize);
                producerChunk.jumpIndex = producerIndex - 1;
                producerChunk.next = chunk;
                producerChunk = chunk;
                this.producerChunk = chunk;
            }
            producerIndex = 0;
        }
        producerChunk.values[producerIndex] = e;
        this.producerIndex = producerIndex + 1;
        size++;
    }

    @Nullable
    public E poll() {
        final Chunk<E> consumerChunk = this.consumerChunk;
        int consumerIndex = this.consumerIndex;
        final E e = consumerChunk.values[consumerIndex];
        if (e == null) {
            return null;
        }
        consumerChunk.values[consumerIndex] = null;
        size--;
        if (consumerIndex == consumerChunk.jumpIndex) {
            consumerIndex = 0;
            this.consumerChunk = consumerChunk.next;
        } else {
            consumerIndex++;
            if (consumerIndex == chunkSize) {
                consumerIndex = 0;
            }
        }
        this.consumerIndex = consumerIndex;
        return e;
    }

    @Nullable
    public E peek() {
        return consumerChunk.values[consumerIndex];
    }

    public void clear() {
        while (poll() != null) {
        }
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        iterator.clear();
        return iterator;
    }

    private static class Chunk<E> {

        final E[] values;
        int jumpIndex = -1;
        Chunk<E> next;

        @SuppressWarnings("unchecked")
        Chunk(final int chunkSize) {
            values = (E[]) new Object[chunkSize];
        }

    }

    private class ChunkedArrayQueueIterator implements Iterator<E> {

        private Chunk<E> iteratorChunk;
        private int iteratorIndex;
        private int iterated;

        void clear() {
            iteratorChunk = consumerChunk;
            iteratorIndex = consumerIndex;
            iterated = 0;
        }

        @Override
        public boolean hasNext() {
            return iterated < size;
        }

        @NotNull
        @Override
        public E next() {
            final E e = iteratorChunk.values[iteratorIndex];
            if (e == null) {
                throw new NoSuchElementException();
            }
            if (iteratorIndex == iteratorChunk.jumpIndex) {
                iteratorIndex = 0;
                iteratorChunk = iteratorChunk.next;
            } else {
                iteratorIndex++;
                if (iteratorIndex == chunkSize) {
                    iteratorIndex = 0;
                }
            }
            iterated++;
            return e;
        }

        @Override
        public void remove() {
            if (iterated != 1) {
                throw new IllegalStateException();
            }
            iterated = 0;
            poll();
        }

    }

}
