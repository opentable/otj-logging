/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opentable.logging;

class AssimilateForeignLoggingHook
{
    private static final String AUTO_ASSIMILATE_PROPERTY = "ot.log.assimilate";

    /**
     * Automatically configure logging without further interaction from the user.
     */
    static void automaticAssimilationHook()
    {
        final String autoAssimilate = System.getProperty(AUTO_ASSIMILATE_PROPERTY);
        if (autoAssimilate == null || Boolean.parseBoolean(autoAssimilate))
        {
            AssimilateForeignLogging.assimilate();
        }
    }
}
