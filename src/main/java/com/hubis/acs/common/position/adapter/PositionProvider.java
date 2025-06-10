package com.hubis.acs.common.position.adapter;

import com.hubis.acs.common.position.model.Position;
/**
 * 로봇 제조사별 위치 정보 제공 인터페이스
 * 모든 로봇의 위치는 Position(x, y, theta) 기반으로 통합
 */
public interface PositionProvider {

    /**
     * 로봇의 현재 위치 반환
     * @param robotId 로봇 ID
     * @return Position (x, y, θ)
     */
    Position getCurrentPosition(String robotId);

    /**
     * 지정된 노드 ID에 해당하는 위치 반환
     * @param nodeId 노드 ID
     * @return Position
     */
    Position getPositionFromNode(String nodeId);

    /**
     * 지정된 Goal ID에 해당하는 위치 반환
     * @param goalId 목표 지점 ID
     * @return Position
     */
    Position getGoalPosition(String goalId);
}

