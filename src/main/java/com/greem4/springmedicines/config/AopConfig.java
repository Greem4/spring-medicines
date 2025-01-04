package com.greem4.springmedicines.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
//fixme: идея с отдельными конфигами для активации различных компонентов мне близка.
// Но в таком виде она не имеет смысла - просто enable-анноташку можно и над Main-классом повесить
// Другое дело, если бы здесь было что-то еще - активация проперти-классов, создание своих бинов и тд.
// Актуально и в некоторых конфигах ниже
@EnableAspectJAutoProxy
public class AopConfig {
}
