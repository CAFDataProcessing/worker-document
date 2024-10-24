/*
 * Copyright 2016-2024 Open Text.
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
package com.hpe.caf.worker.document.testing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CodeConfigurationSourceTest
{
    @Test
    public void testRetrievesConfigurationSuppliedInConstructor() throws Exception
    {
        final TestSource1 source1 = new TestSource1();
        final TestSource2 source2 = new TestSource2();

        final CodeConfigurationSource sut = new CodeConfigurationSource(source1, source2);

        final TestSource1 actualSource1 = sut.getConfiguration(TestSource1.class);
        final TestSource2 actualSource2 = sut.getConfiguration(TestSource2.class);

        assertThat(actualSource1, is(source1));
        assertThat(actualSource2, is(source2));
    }

    @Test
    public void testRetrievesAddedConfiguration() throws Exception
    {
        final TestSource1 source1 = new TestSource1();
        final TestSource2 source2 = new TestSource2();

        final CodeConfigurationSource sut = new CodeConfigurationSource(source1);
        sut.addConfiguration(source2);

        final TestSource1 actualSource1 = sut.getConfiguration(TestSource1.class);
        final TestSource2 actualSource2 = sut.getConfiguration(TestSource2.class);

        assertThat(actualSource1, is(source1));
        assertThat(actualSource2, is(source2));
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    public void testThrowsWhenConfigurationExists() throws Exception
    {
        final TestSource1 source1 = new TestSource1();
        final TestSource2 source2 = new TestSource2();
        final TestSource2 anotherSource2 = new TestSource2();

        final CodeConfigurationSource sut = new CodeConfigurationSource(source1, source2);

        Assertions.assertThrows(IllegalArgumentException.class, () -> sut.addConfiguration(anotherSource2));
    }

    @Test
    public void testCanOverrideConfiguration() throws Exception
    {
        final TestSource1 source1 = new TestSource1();
        final TestSource2 source2 = new TestSource2();
        final TestSource2 anotherSource2 = new TestSource2();

        final CodeConfigurationSource sut = new CodeConfigurationSource(source1, source2);
        sut.addConfiguration(anotherSource2, true);

        final TestSource1 actualSource1 = sut.getConfiguration(TestSource1.class);
        final TestSource2 actualSource2 = sut.getConfiguration(TestSource2.class);

        assertThat(actualSource1, is(source1));
        assertThat(actualSource2, is(anotherSource2));
    }

    class TestSource1
    {
    }

    class TestSource2
    {
    }

    class TestSource3
    {
    }
}
