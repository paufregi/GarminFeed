package paufregi.connectfeed.data.database.coverters

import paufregi.connectfeed.data.database.entities.ProfileEntity
import paufregi.connectfeed.core.models.Profile

fun Profile.toEntity() = ProfileEntity(
    id = id,
    name = name,
    activityType = activityType,
    eventType = eventType,
    course = course,
    water = water,
    rename = rename,
    customWater = customWater,
    feelAndEffort = feelAndEffort,
)

fun ProfileEntity.toCore() = Profile(
    id = id,
    name = name,
    activityType = activityType,
    eventType = eventType,
    course = course,
    water = water,
    rename = rename,
    customWater = customWater,
    feelAndEffort = feelAndEffort,
)