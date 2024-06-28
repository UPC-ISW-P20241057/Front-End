package com.project.medibox.medication.models

data class ConflictingMedicines(
    var id: Long,
    var medicine1Id: Long,
    var medicine1Name: String,
    var medicine2Id: Long,
    var medicine2Name: String
)