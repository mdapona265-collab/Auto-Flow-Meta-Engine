package com.automation.workflow.engine

enum class WorkflowState {
    IDLE, INITIALIZING, EXECUTING, WAITING_FOR_USER, SUCCESS, FAILED
}

sealed class StepAction {
    data class Click(val resourceId: String? = null, val text: String? = null) : StepAction()
    data class InputText(val resourceId: String, val value: String) : StepAction()
    object PauseForOtp : StepAction()
}

data class WorkflowStep(
    val id: Int,
    val description: String,
    val action: StepAction
)

class WorkflowEngine(
    private val steps: List<WorkflowStep>,
    private val onStateChange: (WorkflowState, String) -> Unit
) {
    private var currentIndex = 0

    fun start() {
        currentIndex = 0
        onStateChange(WorkflowState.INITIALIZING, "Workflow engine started.")
        executeNext()
    }

    fun executeNext() {
        if (currentIndex >= steps.size) {
            onStateChange(WorkflowState.SUCCESS, "Workflow finished.")
            return
        }
        val step = steps[currentIndex]
        onStateChange(WorkflowState.EXECUTING, "Running: ${step.description}")
        
        if (step.action is StepAction.PauseForOtp) {
            onStateChange(WorkflowState.WAITING_FOR_USER, "Waiting for user OTP entry.")
        }
    }

    fun resume() {
        currentIndex++
        executeNext()
    }
}
