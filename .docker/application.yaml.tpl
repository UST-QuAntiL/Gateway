server:
  port: {{ .Env.PORT }}

org:
  planqk:
    gateway:
      nisq:
        analyzer:
          uri: {{ .Env.NISQ_ANALYZER_URI }}
      tokens:
        ibmq: {{ .Env.IBMQ_TOKEN }}

logging:
  level:
    org.springframework.cloud.gateway: {{ .Env.LOGGING_LEVEL }}
    org.planqk.gateway: {{ .Env.LOGGING_LEVEL }}
