/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.components

import org.jetbrains.kotlin.builtins.*
import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.inference.model.LowerPriorityToPreserveCompatibility
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.KotlinResolutionCandidate
import org.jetbrains.kotlin.resolve.calls.model.SimpleKotlinCallArgument
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.isDynamic
import org.jetbrains.kotlin.types.typeUtil.isUnit

object UnitTypeConversions : ParameterTypeConversion {
    override fun conversionDefinitelyNotNeeded(
        candidate: KotlinResolutionCandidate,
        argument: KotlinCallArgument,
        expectedParameterType: UnwrappedType
    ): Boolean {
        // for callable references and lambdas it already works
        if (argument !is SimpleKotlinCallArgument) return true

        val argumentType = argument.receiver.stableType
        if (argumentType.isBuiltinFunctionalType && argumentType.getReturnTypeFromFunctionType().isUnitOrDynamic()) return true
        if (argumentType.isKFunctionType && argument.receiver.stableType.arguments.last().type.isUnitOrDynamic()) return true

        if (
            !expectedParameterType.isBuiltinFunctionalType ||
            !expectedParameterType.getReturnTypeFromFunctionType().isUnitOrDynamic()
        ) return true

        return false
    }

    private fun KotlinType.isUnitOrDynamic(): Boolean = isUnit() || isDynamic()

    override fun conversionIsNeededBeforeSubtypingCheck(argument: KotlinCallArgument): Boolean =
        argument is SimpleKotlinCallArgument && argument.receiver.stableType.isFunctionType

    override fun conversionIsNeededAfterSubtypingCheck(argument: KotlinCallArgument): Boolean =
        argument is SimpleKotlinCallArgument &&
                argument.receiver.stableType.isFunctionTypeOrSubtype { !it.getReturnTypeFromFunctionType().isUnit() }

    override fun convertParameterType(
        candidate: KotlinResolutionCandidate,
        argument: KotlinCallArgument,
        parameter: ParameterDescriptor,
        expectedParameterType: UnwrappedType
    ): UnwrappedType? {
        val nonUnitReturnedParameterType = createFunctionType(
            candidate.callComponents.builtIns,
            expectedParameterType.annotations,
            expectedParameterType.getReceiverTypeFromFunctionType(),
            expectedParameterType.getValueParameterTypesFromFunctionType().map { it.type },
            parameterNames = null,
            candidate.callComponents.builtIns.nullableAnyType,
            suspendFunction = expectedParameterType.isSuspendFunctionType
        )

        candidate.resolvedCall.registerArgumentWithUnitConversion(argument, nonUnitReturnedParameterType)

        candidate.addDiagnostic(LowerPriorityToPreserveCompatibility)

        return nonUnitReturnedParameterType
    }

}