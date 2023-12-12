/*
 * Copyright 2023 Google LLC
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
 */

package com.google.ai.client.generativeai.internal.api

import com.google.ai.client.generativeai.internal.api.server.Candidate
import com.google.ai.client.generativeai.internal.api.server.GRpcError
import com.google.ai.client.generativeai.internal.api.server.PromptFeedback
import kotlinx.serialization.Serializable

internal sealed interface Response

@Serializable
internal data class GenerateContentResponse(
  val candidates: List<Candidate>? = null,
  val promptFeedback: PromptFeedback? = null,
) : Response

@Serializable internal data class CountTokensResponse(val totalTokens: Int) : Response

@Serializable internal data class GRpcErrorResponse(val error: GRpcError) : Response
