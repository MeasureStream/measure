package com.polito.tesi.measuremanager.dtos

import measuremanager.measure.dtos.NodeCreateDTO

data class EventNode(val eventType: String, val node: NodeCreateDTO)