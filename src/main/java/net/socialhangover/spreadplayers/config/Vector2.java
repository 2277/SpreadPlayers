package net.socialhangover.spreadplayers.config;


import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Vector2 {

    @NonNull
    private int x;

    @NonNull
    private int y;

}
